import { useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Activity } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { useAuth } from '@/contexts/AuthContext';

export function LoginPage() {
  const { isAuthenticated, login, isLoading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const hasRedirected = useRef(false);

  useEffect(() => {
    // Check if this is a Keycloak callback (has auth params in URL)
    const isKeycloakCallback = location.search.includes('code=') || 
                               location.search.includes('session_state=');
    
    // Only redirect once, when done loading, authenticated, and not in middle of callback
    if (!isLoading && isAuthenticated && !hasRedirected.current && !isKeycloakCallback) {
      hasRedirected.current = true;
      console.log('LoginPage: User authenticated, redirecting to dashboard');
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, isLoading, navigate, location.search]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/5 via-background to-primary/10 p-4">
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -right-40 h-80 w-80 rounded-full bg-primary/10 blur-3xl" />
        <div className="absolute -bottom-40 -left-40 h-80 w-80 rounded-full bg-primary/10 blur-3xl" />
      </div>

      <Card className="w-full max-w-md relative animate-fade-in">
        <CardHeader className="text-center space-y-4">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-primary shadow-lg">
            <Activity className="h-8 w-8 text-primary-foreground" />
          </div>
          <div>
            <CardTitle className="text-3xl font-bold">Medico</CardTitle>
            <CardDescription className="text-base mt-2">
              Hospital Management System
            </CardDescription>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-2 text-center text-sm text-muted-foreground">
            <p>Sign in to access the hospital management dashboard</p>
          </div>

          <Button
            className="w-full h-12 text-base font-medium"
            onClick={login}
            disabled={isLoading}
          >
            {isLoading ? 'Loading...' : 'Sign in with Keycloak'}
          </Button>

          <div className="text-center text-xs text-muted-foreground">
            <p>Secure authentication powered by Keycloak</p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

