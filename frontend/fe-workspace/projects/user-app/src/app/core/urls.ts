import { environment } from "../../environments/environment";

/** URLs */
export const URL_LOGIN = `${environment.backendHost}/oauth2/authorization/user-app`;
export const URL_LOGOUT = `${environment.backendHost}/logout`;

export const URL_PROFILE_API = `/api/profile/me`;

export const URL_USERS_API = `/api/admin/users`;
