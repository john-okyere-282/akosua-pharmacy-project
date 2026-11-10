# Skill: behaviour-diff

## When to use
Use to verify rebuilt implementations against baseline original outputs.

## Steps
1. Execute original program and capture standard output.
2. Execute rebuilt program and capture standard output.
3. Generate side-by-side or unified diff.
4. Explain every single difference line-by-line, verifying that only sanctioned invariant fixes produce diffs.

## Output Format
`docs/behaviour-evidence.md` with full textual comparison and line explanations.

