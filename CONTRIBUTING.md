# Contributing

## Getting Started

1. Fork the repository
2. Create a branch: `git checkout -b feature/your-feature`
3. Make changes and commit: `git commit -m "feat: add feature"`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

## Development Setup

```bash
# Start database
docker-compose up -d db

# Run application
mvn spring-boot:run

# Run tests
mvn test
```

## Commit Convention

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `refactor:` Code refactoring
- `test:` Tests

## Code Style

- Follow Java conventions
- Write unit tests for new features
- Keep methods focused and small
