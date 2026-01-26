import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Layout } from '@/components/layout/Layout';
import { LoginPage } from '@/pages/LoginPage';
import { DashboardPage } from '@/pages/DashboardPage';
import { PatientsPage } from '@/pages/patients/PatientsPage';
import { PatientDetailsPage } from '@/pages/patients/PatientDetailsPage';
import { AppointmentsPage } from '@/pages/appointments/AppointmentsPage';
import { PharmacyPage } from '@/pages/pharmacy/PharmacyPage';
import { InventoryPage } from '@/pages/pharmacy/InventoryPage';
import { LaboratoryPage } from '@/pages/laboratory/LaboratoryPage';
import { BillingPage } from '@/pages/billing/BillingPage';
import { LoadingSpinner } from '@/components/ui/loading-spinner';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();

  console.log('ProtectedRoute:', { isLoading, isAuthenticated });

  // Show loading while Keycloak initializes
  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // Only redirect to login if definitely not authenticated
  if (!isAuthenticated) {
    console.log('ProtectedRoute: Not authenticated, redirecting to login');
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <Layout>
              <Routes>
                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/patients" element={<PatientsPage />} />
                <Route path="/patients/:id" element={<PatientDetailsPage />} />
                <Route path="/appointments" element={<AppointmentsPage />} />
                <Route path="/pharmacy" element={<PharmacyPage />} />
                <Route path="/pharmacy/inventory" element={<InventoryPage />} />
                <Route path="/laboratory" element={<LaboratoryPage />} />
                <Route path="/billing" element={<BillingPage />} />
              </Routes>
            </Layout>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;

