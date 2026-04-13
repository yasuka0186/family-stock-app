# AGENTS.md

## Project Overview
This project is a family shared stock management web application for groceries and household supplies.

The goal is to build a simple, maintainable MVP first, then iteratively improve it.

## Tech Stack
- Backend: Java 17 + Spring Boot
- Frontend: Vue 3 + TypeScript
- Database: PostgreSQL
- Environment: VSCode + Codex

## Development Principles (Most Important)
- Always prioritize MVP (Minimum Viable Product)
- Avoid overengineering
- Prefer simple and maintainable design
- Implement only what is required now
- Clearly separate backend and frontend responsibilities

## MVP Scope Rules
Focus ONLY on the following core features:
- Authentication (basic login)
- Family group sharing
- Stock item management
- Stock quantity update
- Shopping list
- Shared shopping list

### Critical Business Logic
- When `currentStock <= minimumStock`, automatically add to shopping list
- Prevent duplicate shopping list entries
- Ensure consistency between manual and automatic additions
- Define behavior when stock is replenished

## Out of Scope (Do NOT implement unless explicitly asked)
- Barcode scanning
- OCR (receipt scanning)
- Notifications
- Advanced expiration tracking
- Calendar integration
- External price comparison
- Analytics / dashboards
- Complex role/permission systems

## Project Structure
- `/backend` → Spring Boot application
- `/frontend` → Vue 3 application
- `/docs` → design documents (must be created before large implementation)

## Development Workflow
1. Always design before implementing
2. Write design documents under `/docs`
3. Then implement in small steps
4. Validate each step before moving forward

## Implementation Rules
- Do not generate large amounts of code at once
- Break tasks into small steps
- Clearly state assumptions when requirements are unclear
- Keep code readable and beginner-friendly
- Follow standard Spring Boot architecture:
  - Controller
  - Service
  - Repository
  - Domain / Entity
  - DTO

## Frontend Rules
- Use Vue 3 Composition API
- Use TypeScript strictly (no unnecessary `any`)
- Keep components small and reusable
- Separate:
  - components
  - views
  - stores
  - api

## Backend Rules
- Follow layered architecture (Controller / Service / Repository)
- Use DTO for request/response
- Do not expose Entity directly
- Keep business logic inside Service layer

## Database Rules
- Keep schema simple for MVP
- Avoid premature normalization
- Design tables based on actual use cases only

## API Design Rules
- Follow REST conventions
- Keep endpoints simple and predictable
- Ensure stock update triggers shopping list logic

## Output Expectations
When generating responses:
- Be structured and concise
- Show file paths clearly
- Explain reasoning briefly
- Prioritize clarity over completeness

## When in Doubt
- Choose the simpler implementation
- Explicitly state assumptions
- Ask for clarification only if absolutely necessary