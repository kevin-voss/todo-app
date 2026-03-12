Hello # Todo App

A minimal todo application with React (frontend), Java Spring Boot (backend), and H2 SQL (database).

---

## Profile

**Kevin Voß, B.Sc.**  
Senior Fullstack GenAI Software Engineer · Hannover, Germany

### Summary

Senior Full-Stack and GenAI Engineer with 4.5+ years of professional experience delivering scalable cloud-native and AI-powered systems. Leveraging React, TypeScript, Java, Spring Boot, and AWS, I build and maintain reliable platforms while guiding technical decisions across teams. Experienced in system design, infrastructure optimization, and implementing RAG and LLM-based solutions to streamline workflows and accelerate delivery. Skilled in facilitating collaboration between stakeholders, freelancers, and engineers, improving productivity, reducing cloud costs by up to 90%, and supporting faster feature development. Passionate about combining hands-on engineering with innovative AI tooling to deliver practical, high-impact solutions.

### Core Skills

- **AI & GenAI:** LLMs (OpenAI, Claude, Cursor, AI IDEs), RAG (Spring AI), Python, Prompt Engineering, Vector Databases (Qdrant), AI Agents & Sub-Agents, AI Tools/Skills/Rules
- **Frontend:** React, Next.js, TypeScript, Angular, Tailwind CSS, Web Components, Microfrontends
- **Backend:** Java, Kotlin, Spring Boot, Spring AI, Microservices, Node.js, REST APIs
- **Cloud & DevOps:** AWS, Docker, CI/CD, CDK, Terraform, IaC
- **Methods & Tools:** Agile/Scrum, TDD, System Design, Event-Driven Architecture, Git, Jest, Playwright, Mentoring

### Experience

#### Oct 2025 – Present · Software Engineer  
**Adesso SE · Hannover (Hybrid)**

- **GS1 Rule Transformer (GS1):** Designed and implemented a 6-stage GenAI pipeline (Python + OpenAI API) that converts complex German business rules into validated JSON schemas, reducing manual processing time by 95% (30–60 min → <1 min) while eliminating errors
- **WebSocket Load Balancer POC (Mercedes Benz):** Architected a high-availability WebSocket infrastructure for Mercedes-Benz shop-floor systems using Apache HTTP Server and OpenLiberty, improving real-time reliability and reducing connection failures
- **RAG Validator (Siemens AG):** Developed a modular platform to evaluate RAG workflows, ensuring repeatable validation. The system verifies model accuracy and provides suggestions for optimizing file structures, enabling safe and generic use of chatbots across diverse business requirements
- **Tech Stack:** Java, Python, Angular, TypeScript, Apache HTTP Server, OpenAI API, WebSockets, Docker

#### Dec 2021 – Sep 2025 · Mid–Senior Software Engineer  
**Micromerce GmbH · Hamburg (Remote)**

- **End-to-End Contribution:** Directed the technical evolution of the company's core CMS within a 4-person engineering team, covering frontend, backend, and AWS infrastructure. Served as the primary interface between stakeholders, freelancers, customer developers, and engineers, translating requirements into architecture decisions, planning stories, and coordinating complex data migrations
- **Frontend Architecture:** Designed a modular microfrontend setup with React and Next.js that enabled parallel feature work and reduced cross-team dependencies. Delivery speed increased by 40% while code quality improved through structured reviews, automated linting, and consistent testing practices
- **Backend Development:** Developed scalable microservices using Java, Kotlin, and Spring Boot and implemented critical platform capabilities such as resumable uploads, dynamic form builders, LexoRank-based ordering, and automated backup processes to ensure reliability and data integrity across the system
- **Cloud & DevOps:** Leveraging AWS CDK and CloudFormation, refined DynamoDB indexes and access patterns, optimized EC2 instance sizing based on real usage metrics, and introduced CloudWatch-driven auto scaling. These changes reduced infrastructure costs by up to 90% and stabilized performance under varying load. CI/CD pipelines and automated tests shortened deployment cycles by 50%
- **AI & Automation:** Boosted development productivity by up to 50% through the integration of state-of-the-art AI tools (GitHub Copilot, Cursor AI, Claude). Built internal RAG-based GenAI services and leveraged AI to generate comprehensive technical documentation and visualizations, streamlining knowledge transfer
- **Leadership:** Guided 2 junior engineers and 3 working students through mentoring and code reviews, onboarded freelancers and external customer developers, and facilitated technical discussions and knowledge-sharing sessions to maintain consistent engineering standards across the team

### Education

#### Apr 2019 – Mar 2023 · Bachelor of Science (B.Sc.) Business Informatics  
**University of Hildesheim · Hildesheim**

#### Oct 2018 – Mar 2019 · Computer Science  
**Leibniz University Hannover (LUH) · Hannover**

#### Sep 2018 · Abitur  
**Werner von Siemens Schule Hildesheim** · Focus: C++, PHP, SQL, Robotics

### Certifications

- **AWS Certified Developer – Associate** (Expires: Nov 2028)
- **AWS Certified Cloud Practitioner** (Expires: Oct 2028)

### Languages

- **German:** Native
- **English:** Fluent

### Additional Information

- **Work Preferences:** Prefer remote work (open to hybrid). Focused on small companies and startups with a hands-on mentality and fast delivery cycles. Highly adaptable, quickly picking up new skills
- **Interests:** Programming, Cooking, Gym, Piano, Gaming, Concerts, Painting

### Contact

- **Email:** [see LinkedIn/Portfolio]
- **LinkedIn:** [see Portfolio]
- **Portfolio:** [see LinkedIn]
- **Location:** Hannover, Germany

---

## Project Overview

Minimal todo application with **React** (frontend), **Java Spring Boot** (backend), and **H2 SQL** (database). Single-user, no authentication. Features: create, list, update, delete todos; mark complete/incomplete; persistence across restarts.

## Common Commands

| Action | Command |
|--------|---------|
| Frontend dev | `cd frontend && npm run dev` |
| Frontend build | `cd frontend && npm run build` |
| Frontend test | `cd frontend && npm run test` |
| Backend run | `cd backend && ./mvnw spring-boot:run` |
| Backend test | `cd backend && ./mvnw test` |
| All tests | Run both frontend and backend test commands |

## Architecture and Conventions

- **frontend/** — React + Vite + TypeScript. Vitest for tests. Components in `src/components/`, API client in `src/api/`.
- **backend/** — Spring Boot 3.2, Java 17, Maven. JPA + H2. Package: `com.todo`. Layered: entity, repository, service, controller.
- **API base path:** `/api/todos`. REST: GET (list), POST (create), PUT (update), DELETE (delete).
- **Todo shape:** `{ id, title, completed }`. Title required, non-empty.

## Rules and Constraints

- Do NOT modify test files — they are the spec.
- Backend MUST use H2 as sole database (`spring.datasource.url` contains `h2`).
- Frontend MUST export `getTodos`, `createTodo`, `updateTodo`, `deleteTodo` from `src/api/todos`.
- Frontend MUST have `TodoList` component in `src/components/TodoList.tsx`.
- Keep feature set minimal; no auth, no advanced features.

## Key Types

- **Todo:** `{ id: number | string; title: string; completed: boolean }`
- **API:** JSON over HTTP. Create returns 201, update 200, delete 204, not-found 404, validation 400.
