# Exercise - Authentication, Authorization & RBAC

## Objective

Peserta mampu mendesain access control sederhana untuk loan system dengan authentication, authorization, RBAC, resource-level check, dan audit log requirement.

## Case

Gunakan konteks Loan System.

Actors:

- `customer`
- `agent`
- `credit_analyst`
- `supervisor`
- `admin`

Resources:

- `loan_application`
- `loan_approval`
- `report`
- `user_access`

## Task 1 - Explain Authentication vs Authorization

Write explanation:

- authentication
- authorization
- example in loan system

## Task 2 - Design JWT Payload

Create example JWT payload with:

- `user_id`
- `role`
- `branch_id`
- `iss`
- `exp`

Do not include:

- password
- full customer profile
- sensitive customer data
- API secret

Example format:

```json
{
  "user_id": "USR-001",
  "role": "credit_analyst",
  "branch_id": "BR-001",
  "iss": "loan-auth-service",
  "exp": 1710000000
}
```

## Task 3 - Create RBAC Matrix

Create RBAC matrix for:

| Role | Create Loan | View Loan | Approve Loan | View Report | Manage User |
| --- | --- | --- | --- | --- | --- |
| customer | | | | | |
| agent | | | | | |
| credit_analyst | | | | | |
| supervisor | | | | | |
| admin | | | | | |

Rules:

- customer can create own loan.
- customer can view own loan only.
- agent can create/follow up assigned customer loan.
- credit_analyst can approve loan in scope.
- supervisor can view team/branch report.
- admin can manage user access.
- nobody should have all access by default.

## Task 4 - Define Endpoint Access Policy

Write access policy for:

- `POST /api/v1/loan_applications`
- `GET /api/v1/loan_applications/{id}`
- `POST /api/v1/loan_approvals`
- `GET /api/v1/reports/loan_summary`
- `POST /api/v1/users/access`

Each policy must include:

- auth required or not
- allowed role
- resource-level check
- success status
- possible error status

Suggested format:

```text
Endpoint:
POST /api/v1/loan_approvals

Auth required:
Yes, Bearer token

Allowed role:
credit_analyst

Resource-level check:
loan_application branch must match analyst branch scope

Success status:
201 Created

Possible error status:
401 Unauthorized
403 Forbidden
404 Not Found
```

## Task 5 - Define 401 and 403 Error Response

Create example JSON for:

- 401 Unauthorized
- 403 Forbidden

Each response must include:

- `status`
- `error_code`
- `message`
- `correlation_id`

Example 401:

```json
{
  "status": 401,
  "error_code": "UNAUTHORIZED",
  "message": "Authentication required",
  "correlation_id": "REQ-20260424-001"
}
```

Example 403:

```json
{
  "status": 403,
  "error_code": "FORBIDDEN",
  "message": "Access denied",
  "correlation_id": "REQ-20260424-002"
}
```

## Task 6 - Write Resource Ownership Scenario

Create scenario:

- `authenticated_customer_id` is `CUST-001`
- `requested_customer_id` is `CUST-999`
- endpoint is `GET /api/v1/loan_applications/{id}`

Explain:

- should this be allowed?
- what status should be returned?
- why?

## Task 7 - Write Loan Approval Scenario

Scenario:

- agent has valid token.
- agent calls `POST /api/v1/loan_approvals`.
- endpoint requires `credit_analyst` role.

Explain:

- authentication result
- authorization result
- expected HTTP status
- expected error response
- expected audit log result

## Task 8 - Create Access Log Design

Define access log fields:

- `correlation_id`
- `user_id`
- `role`
- `endpoint`
- `action`
- `resource_id`
- `result`
- `reason`
- `created_at`

Explain purpose of each field.

Suggested format:

| Field | Purpose | Example |
| --- | --- | --- |
| correlation_id | trace request | REQ-20260424-001 |
| user_id | identify actor | USR-001 |
| role | access context | agent |
| endpoint | target API | POST /api/v1/loan_approvals |
| action | business action | approve_loan |
| resource_id | target resource | LOAN-001 |
| result | allow/deny result | 403 FORBIDDEN |
| reason | short reason | role_not_allowed |
| created_at | event time | 2026-04-24T10:15:30Z |

## Task 9 - Update API Contract with Auth Requirement

Choose one endpoint:

```text
POST /api/v1/loan_approvals
```

Write API contract including:

- method
- URL
- description
- auth requirement
- allowed role
- resource-level check
- request body
- success response
- 401 response
- 403 response
- status code

Example request body:

```json
{
  "loan_application_id": "LOAN-001",
  "decision": "approved",
  "notes": "Applicant meets policy requirement"
}
```

Example success response:

```json
{
  "approval_id": "APR-001",
  "loan_application_id": "LOAN-001",
  "decision": "approved",
  "approved_by": "USR-001"
}
```

## Acceptance Criteria

- [ ] Authentication and authorization are explained correctly.
- [ ] JWT payload example contains safe claims.
- [ ] JWT payload does not contain sensitive data.
- [ ] RBAC matrix is created.
- [ ] Endpoint access policy is created.
- [ ] 401 response is used for missing/invalid/expired token.
- [ ] 403 response is used for valid user without access.
- [ ] Resource ownership scenario is explained.
- [ ] Loan approval scenario is explained.
- [ ] Access log fields are defined.
- [ ] API contract includes auth requirement.
- [ ] API contract includes role requirement.
- [ ] API contract includes possible security error responses.

## Optional Challenge

- Add permission-based matrix in addition to role matrix.
- Add branch scope examples.
- Add denied access audit log example.
- Add Swagger/OpenAPI security scheme explanation.
- Add pseudo-code for checking role and resource ownership.
