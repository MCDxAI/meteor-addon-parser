#!/usr/bin/env python
import argparse
import csv
import json
import subprocess
import sys
from datetime import datetime, timezone
from pathlib import Path


SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent
DEFAULT_ADDONS_DIR = PROJECT_ROOT / "ai_reference" / "addons"
DEFAULT_MANIFEST = SCRIPT_DIR / "addon_repos.csv"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Sync addon source repositories for ai_reference/addons. "
            "Release jar downloading now lives in Java via Gradle task "
            "'downloadLatestReleaseJars'."
        )
    )
    parser.add_argument(
        "--addons-dir",
        default=str(DEFAULT_ADDONS_DIR),
        help=f"Directory where addon repos are cloned (default: {DEFAULT_ADDONS_DIR})",
    )
    parser.add_argument(
        "--manifest",
        default=str(DEFAULT_MANIFEST),
        help=f"CSV manifest with columns folder,repo_url (default: {DEFAULT_MANIFEST})",
    )
    parser.add_argument(
        "--update",
        action="store_true",
        help="If set, also fetch and fast-forward pull repos that already exist.",
    )
    parser.add_argument(
        "--depth",
        type=int,
        default=1,
        help="Depth to use for new clones (default: 1; use <=0 for full history).",
    )
    parser.add_argument(
        "--summary-json",
        default="",
        help="Optional output file for clone summary JSON (default: <addons-dir>/clone-summary.json).",
    )
    return parser.parse_args()


def run_git(args: list[str], cwd: Path | None = None) -> tuple[int, str]:
    proc = subprocess.run(
        args,
        cwd=str(cwd) if cwd else None,
        capture_output=True,
        text=True,
        check=False,
    )
    combined = "\n".join(x for x in [proc.stdout.strip(), proc.stderr.strip()] if x).strip()
    return proc.returncode, combined


def normalize_remote(url: str) -> str:
    trimmed = url.strip()
    if trimmed.endswith(".git"):
        trimmed = trimmed[: -len(".git")]
    return trimmed.rstrip("/")


def load_manifest(manifest_path: Path) -> list[dict]:
    if not manifest_path.is_file():
        raise FileNotFoundError(f"manifest not found: {manifest_path}")

    with manifest_path.open("r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        required = {"folder", "repo_url"}
        if reader.fieldnames is None or not required.issubset(set(reader.fieldnames)):
            raise ValueError("manifest must include header columns: folder,repo_url")

        rows = []
        seen = set()
        for row in reader:
            folder = (row.get("folder") or "").strip()
            repo_url = (row.get("repo_url") or "").strip()
            if not folder or not repo_url:
                continue
            key = folder.lower()
            if key in seen:
                raise ValueError(f"duplicate folder in manifest: {folder}")
            seen.add(key)
            rows.append({"folder": folder, "repo_url": repo_url})
        return rows


def clone_repo(repo_url: str, target_dir: Path, depth: int) -> tuple[bool, str]:
    clone_cmd = ["git", "clone"]
    if depth > 0:
        clone_cmd += ["--depth", str(depth)]
    clone_cmd += [repo_url, str(target_dir)]
    code, out = run_git(clone_cmd)
    return code == 0, out


def update_repo(repo_dir: Path) -> tuple[bool, str]:
    fetch_code, fetch_out = run_git(
        ["git", "-C", str(repo_dir), "fetch", "--all", "--tags", "--prune"]
    )
    if fetch_code != 0:
        return False, fetch_out

    pull_code, pull_out = run_git(["git", "-C", str(repo_dir), "pull", "--ff-only"])
    if pull_code != 0:
        return False, pull_out
    return True, "\n".join(x for x in [fetch_out, pull_out] if x).strip()


def get_origin(repo_dir: Path) -> str:
    code, out = run_git(["git", "-C", str(repo_dir), "remote", "get-url", "origin"])
    if code != 0:
        return ""
    return out.strip()


def main() -> int:
    args = parse_args()
    addons_dir = Path(args.addons_dir).resolve()
    manifest_path = Path(args.manifest).resolve()
    summary_json = (
        Path(args.summary_json).resolve()
        if args.summary_json.strip()
        else addons_dir / "clone-summary.json"
    )

    try:
        manifest_rows = load_manifest(manifest_path)
    except Exception as e:
        print(str(e), file=sys.stderr)
        return 2

    addons_dir.mkdir(parents=True, exist_ok=True)

    entries: list[dict] = []
    failure_statuses = {
        "path_exists_not_git",
        "origin_mismatch",
        "clone_failed",
        "update_failed",
    }

    for row in manifest_rows:
        folder = row["folder"]
        repo_url = row["repo_url"]
        target = addons_dir / folder
        status = "unknown"
        note = ""

        if target.exists() and not (target / ".git").exists():
            status = "path_exists_not_git"
            note = "target path exists but is not a git repository"
        elif (target / ".git").exists():
            current_origin = get_origin(target)
            expected = normalize_remote(repo_url)
            actual = normalize_remote(current_origin)
            if not current_origin:
                status = "origin_mismatch"
                note = "unable to read origin remote"
            elif expected != actual:
                status = "origin_mismatch"
                note = f"expected_origin={repo_url};actual_origin={current_origin}"
            elif args.update:
                ok, output = update_repo(target)
                status = "updated" if ok else "update_failed"
                note = output
            else:
                status = "exists"
        else:
            ok, output = clone_repo(repo_url, target, args.depth)
            status = "cloned" if ok else "clone_failed"
            note = output

        entries.append(
            {
                "folder": folder,
                "repo_url": repo_url,
                "target_dir": str(target),
                "status": status,
                "note": note,
            }
        )

    summary_json.parent.mkdir(parents=True, exist_ok=True)
    summary_json.write_text(json.dumps(entries, indent=2), encoding="utf-8")

    cloned = sum(1 for e in entries if e["status"] == "cloned")
    exists = sum(1 for e in entries if e["status"] == "exists")
    updated = sum(1 for e in entries if e["status"] == "updated")
    failed = sum(1 for e in entries if e["status"] in failure_statuses)

    lines = [
        f"timestamp_utc={datetime.now(timezone.utc).isoformat()}",
        f"addons_dir={addons_dir}",
        f"manifest={manifest_path}",
        f"repos_total={len(entries)}",
        f"cloned={cloned}",
        f"exists={exists}",
        f"updated={updated}",
        f"failures={failed}",
        f"summary_json={summary_json}",
        "next_step=Run `gradle downloadLatestReleaseJars --no-daemon` to fetch release jars.",
    ]
    print("\n".join(lines))

    return 1 if failed > 0 else 0


if __name__ == "__main__":
    raise SystemExit(main())
