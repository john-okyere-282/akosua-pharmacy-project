# Java Style & Engineering Rules

- Target JDK 21. No external runtime dependencies; only JUnit 5 for tests.
- Private fields, immutability by default, records for pure value types/events.
- Sealed types where variants are closed; enums only for fixed sets; strategy/polymorphism where open for extension.
- No public mutable state. Never return internal mutable collections; return unmodifiable views or defensive copies (`Collections.unmodifiableList`, `List.copyOf`).
- Constructors validate their arguments (`Objects.requireNonNull`, positive quantity checks). Invalid objects must be impossible to construct.
- Comments explain WHY, not WHAT. Plain, short English.

