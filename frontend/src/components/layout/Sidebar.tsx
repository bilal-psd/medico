import { Link, useLocation } from 'react-router-dom';
import { cn } from '@/lib/utils';
import {
  LayoutDashboard,
  Users,
  Calendar,
  Pill,
  Package,
  FlaskConical,
  Receipt,
  Settings,
  X,
  Activity,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

const navigation = [
  { name: 'Dashboard', href: '/dashboard', icon: LayoutDashboard, roles: ['admin', 'doctor', 'receptionist'] },
  { name: 'Patients', href: '/patients', icon: Users, roles: ['admin', 'doctor', 'receptionist'] },
  { name: 'Appointments', href: '/appointments', icon: Calendar, roles: ['admin', 'doctor', 'receptionist'] },
  { name: 'Pharmacy', href: '/pharmacy', icon: Pill, roles: ['admin', 'pharmacist'] },
  { name: 'Inventory', href: '/pharmacy/inventory', icon: Package, roles: ['admin', 'pharmacist'] },
  { name: 'Laboratory', href: '/laboratory', icon: FlaskConical, roles: ['admin', 'doctor', 'lab_technician'] },
  { name: 'Billing', href: '/billing', icon: Receipt, roles: ['admin', 'billing_staff', 'receptionist'] },
];

export function Sidebar({ open, onClose }: SidebarProps) {
  const location = useLocation();
  const { hasAnyRole } = useAuth();

  const filteredNavigation = navigation.filter((item) => hasAnyRole(item.roles));

  return (
    <>
      {/* Mobile backdrop */}
      {open && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          'fixed inset-y-0 left-0 z-50 flex w-64 flex-col bg-card border-r transition-transform duration-300 lg:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full lg:w-20'
        )}
      >
        {/* Logo */}
        <div className="flex h-16 items-center justify-between border-b px-4">
          <Link to="/dashboard" className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary">
              <Activity className="h-6 w-6 text-primary-foreground" />
            </div>
            {open && (
              <span className="text-xl font-bold text-foreground">Medico</span>
            )}
          </Link>
          <Button
            variant="ghost"
            size="icon"
            className="lg:hidden"
            onClick={onClose}
          >
            <X className="h-5 w-5" />
          </Button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 space-y-1 p-4">
          {filteredNavigation.map((item) => {
            const isActive = location.pathname === item.href;
            return (
              <Link
                key={item.name}
                to={item.href}
                className={cn(
                  'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-primary text-primary-foreground'
                    : 'text-muted-foreground hover:bg-muted hover:text-foreground'
                )}
              >
                <item.icon className="h-5 w-5 shrink-0" />
                {open && <span>{item.name}</span>}
              </Link>
            );
          })}
        </nav>

        {/* Settings */}
        <div className="border-t p-4">
          <Link
            to="/settings"
            className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
          >
            <Settings className="h-5 w-5 shrink-0" />
            {open && <span>Settings</span>}
          </Link>
        </div>
      </aside>
    </>
  );
}

