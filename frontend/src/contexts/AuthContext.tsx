import { createContext, useContext, useEffect, useState, useCallback, useRef } from 'react';
import Keycloak from 'keycloak-js';
import { setAuthToken } from '@/lib/api';

interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: User | null;
  token: string | null;
  login: () => void;
  logout: () => void;
  hasRole: (role: string) => boolean;
  hasAnyRole: (roles: string[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8180',
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'medico',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'medico-frontend',
});

// Track initialization to prevent double-init in React Strict Mode
let keycloakInitialized = false;

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const isInitializing = useRef(false);

  const updateUserInfo = useCallback(() => {
    if (keycloak.authenticated && keycloak.tokenParsed) {
      const tokenParsed = keycloak.tokenParsed as {
        sub: string;
        preferred_username: string;
        email: string;
        given_name: string;
        family_name: string;
        realm_access?: { roles: string[] };
      };

      setUser({
        id: tokenParsed.sub,
        username: tokenParsed.preferred_username,
        email: tokenParsed.email,
        firstName: tokenParsed.given_name,
        lastName: tokenParsed.family_name,
        roles: tokenParsed.realm_access?.roles || [],
      });
      const token = keycloak.token || null;
      setToken(token);
      setAuthToken(token); // Sync token with API client
      setIsAuthenticated(true);
    }
  }, []);

  useEffect(() => {
    // Prevent double initialization (React Strict Mode calls useEffect twice)
    if (keycloakInitialized || isInitializing.current) {
      // If already initialized, sync state from keycloak instance
      if (keycloak.authenticated) {
        updateUserInfo();
      }
      setIsLoading(false);
      return;
    }

    isInitializing.current = true;

    keycloak
      .init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false,
        silentCheckSsoFallback: false,
      })
      .then((authenticated) => {
        console.log('Keycloak initialized, authenticated:', authenticated);
        keycloakInitialized = true;
        setIsAuthenticated(authenticated);
        if (authenticated) {
          updateUserInfo();
        }
        setIsLoading(false);
      })
      .catch((error) => {
        console.error('Keycloak init error:', error);
        keycloakInitialized = true;
        setIsAuthenticated(false);
        setIsLoading(false);
      });

    // Handle token expiry - refresh silently without changing auth state
    keycloak.onTokenExpired = () => {
      keycloak
        .updateToken(30)
        .then((refreshed) => {
          if (refreshed) {
            const newToken = keycloak.token || null;
            setToken(newToken);
            setAuthToken(newToken); // Sync with API client
          }
        })
        .catch(() => {
          // Only logout if refresh truly fails
          console.error('Token refresh failed');
          setIsAuthenticated(false);
          setUser(null);
          setToken(null);
          setAuthToken(null); // Clear API client token
        });
    };
  }, [updateUserInfo]);

  const login = useCallback(() => {
    // Redirect to dashboard after successful login (not back to login page)
    keycloak.login({ redirectUri: window.location.origin + '/dashboard' });
  }, []);

  const logout = useCallback(() => {
    setAuthToken(null); // Clear API client token
    keycloak.logout({ redirectUri: window.location.origin + '/login' });
  }, []);

  const hasRole = useCallback(
    (role: string) => {
      return user?.roles.includes(role) || user?.roles.includes('admin') || false;
    },
    [user]
  );

  const hasAnyRole = useCallback(
    (roles: string[]) => {
      return roles.some((role) => hasRole(role));
    },
    [hasRole]
  );

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        isLoading,
        user,
        token,
        login,
        logout,
        hasRole,
        hasAnyRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

