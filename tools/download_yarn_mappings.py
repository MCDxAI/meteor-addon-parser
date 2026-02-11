#!/usr/bin/env python
import pathlib
import re
import urllib.request

PROJECT_ROOT = pathlib.Path(__file__).resolve().parent.parent
ROOT = PROJECT_ROOT / "ai_reference" / "addons"
OUT_DIR = PROJECT_ROOT / "mappings"
MAVEN_BASE = "https://maven.fabricmc.net/net/fabricmc/yarn/{version}/yarn-{version}-v2.jar"

PATTERNS = [
    re.compile(r"yarn_mappings\s*=\s*([0-9]+\.[0-9]+\.[0-9]+\+build\.[0-9]+)"),
    re.compile(r"yarn-mappings\s*=\s*\"([0-9]+\.[0-9]+\.[0-9]+\+build\.[0-9]+)\""),
    re.compile(r"yarn-mappings\s*=\s*'([0-9]+\.[0-9]+\.[0-9]+\+build\.[0-9]+)'"),
]


def collect_versions(root: pathlib.Path) -> set[str]:
    versions: set[str] = set()
    if not root.is_dir():
        return versions

    for path in root.rglob("*"):
        if not path.is_file():
            continue
        if path.suffix.lower() not in {".properties", ".toml", ".gradle", ".kts", ".txt", ".md"}:
            continue
        try:
            text = path.read_text(encoding="utf-8", errors="ignore")
        except Exception:
            continue
        for pattern in PATTERNS:
            for match in pattern.finditer(text):
                versions.add(match.group(1))
    return versions


def download(version: str, out_dir: pathlib.Path) -> tuple[bool, pathlib.Path, str]:
    out_dir.mkdir(parents=True, exist_ok=True)
    out = out_dir / f"yarn-{version}-v2.jar"
    if out.exists() and out.stat().st_size > 0:
        return True, out, "exists"

    url = MAVEN_BASE.format(version=version)
    try:
        urllib.request.urlretrieve(url, out)
        return True, out, "downloaded"
    except Exception as e:
        return False, out, str(e)


def main() -> int:
    versions = sorted(collect_versions(ROOT))
    print(f"discovered versions: {len(versions)}")
    for v in versions:
        print(f"  {v}")

    ok = True
    for version in versions:
        success, out, status = download(version, OUT_DIR)
        if success:
            print(f"[OK] {version} -> {out} ({status})")
        else:
            ok = False
            print(f"[FAIL] {version} -> {out} ({status})")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
