feat(auth): implement vertical slice for Authentication [P_FE5/P_FE6]

## Description
Implemented the full vertical slice for the Authentication feature, adhering strictly to L3 Frontend conventions (Smart/Dumb component separation). 

## Changes Made
* **Types:** Mapped API response models from `01_auth.yaml` into `auth.types.ts`.
* **Services:** Created `auth.service.ts` using the standardized `apiClient` wrapper (supports generic `ApiResponse<T>`).
* **Hooks:** Developed `useAuthMutations` to decouple loading/error state management and Zustand store hydration from the UI.
* **Components (Dumb):** 
  * `LoginForm.tsx`: Handles email/pass input, local validation, password visibility toggle, and maps field-level `ApiError`.
  * `SsoButtons.tsx`: Renders Azure AD and Google Workspace login actions with generic SVGs.
* **Pages (Smart):** 
  * `LoginPage.tsx`: Assembles dumb components, handles route redirection (`state.from`), and orchestrates API mutations.

## QA & Testing
- [x] Tested email/password flow.
- [x] Verified loading skeletons/spinners are active during mutations.
- [x] Validated field-level error mapping (red borders on input) and global Toast notifications for 401/500 errors.
- [x] Checked against Prop Drilling (>3 levels) and Axios-in-useEffect anti-patterns (0 violations found).

**Ticket:** ATP-901
