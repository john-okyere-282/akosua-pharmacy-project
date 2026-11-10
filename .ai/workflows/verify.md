# Workflow: Verification & Testing

1. Compare execution output against `baseline/original-output.txt`.
2. Document diff in `docs/behaviour-evidence.md`.
3. Author comprehensive JUnit 5 test suite covering characterization, invariants, encapsulation, and extensibility.
4. Verify all tests pass cleanly under `mvn test`.

