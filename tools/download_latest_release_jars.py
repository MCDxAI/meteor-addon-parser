#!/usr/bin/env python
import argparse
import csv
import json
import os
import re
import subprocess
import sys
import urllib.error
import urllib.request
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, List, Optional, Tuple


GITHUB_API = "https://api.github.com"
USER_AGENT = "addon-release-fetcher/1.0"
SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent
DEFAULT_ADDONS_DIR = PROJECT_ROOT / "ai_reference" / "addons"
DEFAULT_JARS_DIR = PROJECT_ROOT / "fixtures" / "addons" / "jars"


def get_token() -> Optional[str]:
    return os.environ.get("GITHUB_TOKEN") or os.environ.get("GH_TOKEN")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Download latest GitHub release jar assets for all addon repos."
    )
    parser.add_argument(
        "--addons-dir",
        default=str(DEFAULT_ADDONS_DIR),
        help=f"Directory containing addon git repos (default: {DEFAULT_ADDONS_DIR})",
    )
    parser.add_argument(
        "--jars-dir",
        default=str(DEFAULT_JARS_DIR),
        help=f"Output directory for downloaded fixture jars (default: {DEFAULT_JARS_DIR})",
    )
    return parser.parse_args()


def api_get_json(path: str, token: Optional[str]) -> Tuple[Optional[object], Optional[str], int]:
    url = f"{GITHUB_API}{path}"
    headers = {
        "Accept": "application/vnd.github+json",
        "User-Agent": USER_AGENT,
    }
    if token:
        headers["Authorization"] = f"Bearer {token}"

    req = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=60) as resp:
            status = getattr(resp, "status", 200)
            raw = resp.read().decode("utf-8", errors="replace")
            return json.loads(raw), None, status
    except urllib.error.HTTPError as e:
        try:
            body = e.read().decode("utf-8", errors="replace")
        except Exception:
            body = str(e)
        return None, f"HTTP {e.code}: {body}", e.code
    except Exception as e:
        return None, str(e), -1


def download_file(url: str, out_path: Path, token: Optional[str]) -> Tuple[bool, str]:
    headers = {"User-Agent": USER_AGENT}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=120) as resp:
            data = resp.read()
        out_path.write_bytes(data)
        if out_path.stat().st_size <= 0:
            return False, "downloaded file was empty"
        return True, ""
    except Exception as e:
        return False, str(e)


def get_origin_url(repo_dir: Path) -> Optional[str]:
    try:
        p = subprocess.run(
            ["git", "-C", str(repo_dir), "remote", "get-url", "origin"],
            capture_output=True,
            text=True,
            check=False,
        )
        if p.returncode != 0:
            return None
        origin = p.stdout.strip()
        return origin or None
    except Exception:
        return None


def parse_github_slug(origin: str) -> Optional[str]:
    patterns = [
        r"github\.com[:/](?P<slug>[^/]+/[^/.]+?)(?:\.git)?$",
        r"^https?://github\.com/(?P<slug>[^/]+/[^/.]+?)(?:\.git)?/?$",
        r"^git@github\.com:(?P<slug>[^/]+/[^/.]+?)(?:\.git)?$",
    ]
    for pat in patterns:
        m = re.search(pat, origin)
        if m:
            return m.group("slug")
    return None


def get_latest_release(slug: str, token: Optional[str]) -> Tuple[Optional[Dict], str, str]:
    latest, err, code = api_get_json(f"/repos/{slug}/releases/latest", token)
    if latest is not None and isinstance(latest, dict):
        return latest, "latest", ""

    releases, err2, _ = api_get_json(f"/repos/{slug}/releases?per_page=1", token)
    if releases is not None and isinstance(releases, list) and len(releases) > 0 and isinstance(releases[0], dict):
        return releases[0], "releases_list_first", ""

    if err2:
        return None, "", err2
    if err:
        return None, "", err
    if code == 404:
        return None, "", "no releases found"
    return None, "", "unable to resolve latest release"


def sanitize_filename(name: str) -> str:
    return re.sub(r'[<>:"/\\|?*]+', "_", name)


def main() -> int:
    args = parse_args()
    addons_dir = Path(args.addons_dir).resolve()
    jars_dir = Path(args.jars_dir).resolve()
    jars_dir.mkdir(parents=True, exist_ok=True)
    token = get_token()

    if not addons_dir.is_dir():
        print(f"addons directory not found: {addons_dir}", file=sys.stderr)
        return 2

    entries: List[Dict] = []

    repo_dirs = sorted(
        [d for d in addons_dir.iterdir() if d.is_dir() and d.name.lower() != "jars"],
        key=lambda p: p.name.lower(),
    )

    for addon_dir in repo_dirs:
        git_dir = addon_dir / ".git"
        if not git_dir.exists():
            entries.append(
                {
                    "addon_folder": addon_dir.name,
                    "repo": "",
                    "release_tag": "",
                    "release_name": "",
                    "release_url": "",
                    "published_at": "",
                    "releases_url": "",
                    "asset_name": "",
                    "asset_url": "",
                    "downloaded_to": "",
                    "status": "not_a_git_repo",
                    "note": "",
                }
            )
            continue

        origin = get_origin_url(addon_dir)
        if not origin:
            entries.append(
                {
                    "addon_folder": addon_dir.name,
                    "repo": "",
                    "release_tag": "",
                    "release_name": "",
                    "release_url": "",
                    "published_at": "",
                    "releases_url": "",
                    "asset_name": "",
                    "asset_url": "",
                    "downloaded_to": "",
                    "status": "missing_origin",
                    "note": "",
                }
            )
            continue

        slug = parse_github_slug(origin)
        if not slug:
            entries.append(
                {
                    "addon_folder": addon_dir.name,
                    "repo": origin,
                    "release_tag": "",
                    "release_name": "",
                    "release_url": "",
                    "published_at": "",
                    "releases_url": "",
                    "asset_name": "",
                    "asset_url": "",
                    "downloaded_to": "",
                    "status": "non_github_or_unparsed_remote",
                    "note": "",
                }
            )
            continue

        release, source, release_err = get_latest_release(slug, token)
        releases_url = f"https://github.com/{slug}/releases"
        if not release:
            entries.append(
                {
                    "addon_folder": addon_dir.name,
                    "repo": slug,
                    "release_tag": "",
                    "release_name": "",
                    "release_url": "",
                    "published_at": "",
                    "releases_url": releases_url,
                    "asset_name": "",
                    "asset_url": "",
                    "downloaded_to": "",
                    "status": "no_releases_found",
                    "note": release_err,
                }
            )
            continue

        assets = release.get("assets") or []
        jar_assets = [a for a in assets if str(a.get("name", "")).lower().endswith(".jar")]

        if not jar_assets:
            entries.append(
                {
                    "addon_folder": addon_dir.name,
                    "repo": slug,
                    "release_tag": release.get("tag_name", ""),
                    "release_name": release.get("name", ""),
                    "release_url": release.get("html_url", ""),
                    "published_at": release.get("published_at", ""),
                    "releases_url": releases_url,
                    "asset_name": "",
                    "asset_url": "",
                    "downloaded_to": "",
                    "status": "release_found_no_jar_assets",
                    "note": f"source={source}",
                }
            )
            continue

        for asset in jar_assets:
            asset_name = str(asset.get("name", "")).strip()
            asset_url = str(asset.get("browser_download_url", "")).strip()
            if not asset_name or not asset_url:
                entries.append(
                    {
                        "addon_folder": addon_dir.name,
                        "repo": slug,
                        "release_tag": release.get("tag_name", ""),
                        "release_name": release.get("name", ""),
                        "release_url": release.get("html_url", ""),
                        "published_at": release.get("published_at", ""),
                        "releases_url": releases_url,
                        "asset_name": asset_name,
                        "asset_url": asset_url,
                        "downloaded_to": "",
                        "status": "failed_download",
                        "note": "asset missing name/url",
                    }
                )
                continue

            out_name = sanitize_filename(f"{addon_dir.name}--{asset_name}")
            out_path = jars_dir / out_name
            ok, err = download_file(asset_url, out_path, token)

            entries.append(
                {
                    "addon_folder": addon_dir.name,
                    "repo": slug,
                    "release_tag": release.get("tag_name", ""),
                    "release_name": release.get("name", ""),
                    "release_url": release.get("html_url", ""),
                    "published_at": release.get("published_at", ""),
                    "releases_url": releases_url,
                    "asset_name": asset_name,
                    "asset_url": asset_url,
                    "downloaded_to": str(out_path) if ok else "",
                    "status": "downloaded" if ok else "failed_download",
                    "note": f"source={source}" if ok else f"source={source};error={err}",
                }
            )

    summary_json = jars_dir / "release-summary.json"
    summary_csv = jars_dir / "release-summary.csv"
    summary_txt = jars_dir / "release-summary.txt"

    summary_json.write_text(json.dumps(entries, indent=2), encoding="utf-8")
    if entries:
        with summary_csv.open("w", encoding="utf-8", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=list(entries[0].keys()))
            writer.writeheader()
            writer.writerows(entries)
    else:
        summary_csv.write_text("", encoding="utf-8")

    downloaded_count = sum(1 for e in entries if e["status"] == "downloaded")
    repo_count = len({e["addon_folder"] for e in entries})
    no_release = sum(1 for e in entries if e["status"] == "no_releases_found")
    no_jar = sum(1 for e in entries if e["status"] == "release_found_no_jar_assets")
    failed = sum(1 for e in entries if e["status"] == "failed_download")

    lines = [
        f"timestamp_utc={datetime.now(timezone.utc).isoformat()}",
        f"addons_dir={addons_dir}",
        f"jars_dir={jars_dir}",
        f"repos_seen={repo_count}",
        f"downloaded_jars={downloaded_count}",
        f"no_releases={no_release}",
        f"releases_without_jars={no_jar}",
        f"failed_downloads={failed}",
        f"summary_json={summary_json}",
        f"summary_csv={summary_csv}",
    ]
    summary_txt.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print("\n".join(lines))
    return 0


if __name__ == "__main__":
    sys.exit(main())
