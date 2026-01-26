import { useQuery } from '@tanstack/react-query';
import {
  Users,
  Calendar,
  Pill,
  FlaskConical,
  DollarSign,
  AlertTriangle,
  Clock,
  TrendingUp,
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get } from '@/lib/api';
import { formatCurrency, formatNumber } from '@/lib/utils';

interface DashboardStats {
  totalPatients: number;
  todayAppointments: number;
  pendingAppointments: number;
  totalMedications: number;
  lowStockItems: number;
  expiringItems: number;
  todayDispensings: number;
  pendingLabOrders: number;
  todayLabOrders: number;
  pendingVerifications: number;
  todayRevenue: number;
  pendingInvoices: number;
  overdueInvoices: number;
  activePrescriptions: number;
}

export function DashboardPage() {
  const { data: stats, isLoading } = useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: () => get<DashboardStats>('/dashboard/stats'),
  });

  if (isLoading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  const statCards = [
    {
      title: 'Total Patients',
      value: formatNumber(stats?.totalPatients || 0),
      icon: Users,
      color: 'text-blue-600 bg-blue-100',
    },
    {
      title: "Today's Appointments",
      value: formatNumber(stats?.todayAppointments || 0),
      icon: Calendar,
      color: 'text-green-600 bg-green-100',
    },
    {
      title: 'Active Prescriptions',
      value: formatNumber(stats?.activePrescriptions || 0),
      icon: Pill,
      color: 'text-purple-600 bg-purple-100',
    },
    {
      title: 'Pending Lab Orders',
      value: formatNumber(stats?.pendingLabOrders || 0),
      icon: FlaskConical,
      color: 'text-orange-600 bg-orange-100',
    },
    {
      title: "Today's Revenue",
      value: formatCurrency(stats?.todayRevenue || 0),
      icon: DollarSign,
      color: 'text-emerald-600 bg-emerald-100',
    },
    {
      title: 'Low Stock Items',
      value: formatNumber(stats?.lowStockItems || 0),
      icon: AlertTriangle,
      color: 'text-red-600 bg-red-100',
    },
    {
      title: 'Pending Invoices',
      value: formatNumber(stats?.pendingInvoices || 0),
      icon: Clock,
      color: 'text-amber-600 bg-amber-100',
    },
    {
      title: 'Overdue Invoices',
      value: formatNumber(stats?.overdueInvoices || 0),
      icon: TrendingUp,
      color: 'text-rose-600 bg-rose-100',
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground">
          Welcome back! Here's an overview of your hospital.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {statCards.map((stat) => (
          <Card key={stat.title} className="card-hover">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                {stat.title}
              </CardTitle>
              <div className={`rounded-lg p-2 ${stat.color}`}>
                <stat.icon className="h-4 w-4" />
              </div>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stat.value}</div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Recent Activity</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <div className="h-2 w-2 rounded-full bg-green-500" />
                <div className="flex-1">
                  <p className="text-sm font-medium">New patient registered</p>
                  <p className="text-xs text-muted-foreground">2 minutes ago</p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <div className="h-2 w-2 rounded-full bg-blue-500" />
                <div className="flex-1">
                  <p className="text-sm font-medium">Appointment completed</p>
                  <p className="text-xs text-muted-foreground">15 minutes ago</p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <div className="h-2 w-2 rounded-full bg-purple-500" />
                <div className="flex-1">
                  <p className="text-sm font-medium">Lab results ready</p>
                  <p className="text-xs text-muted-foreground">1 hour ago</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Upcoming Appointments</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium">John Smith</p>
                  <p className="text-xs text-muted-foreground">General Checkup</p>
                </div>
                <span className="text-sm text-muted-foreground">10:00 AM</span>
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium">Jane Doe</p>
                  <p className="text-xs text-muted-foreground">Follow-up</p>
                </div>
                <span className="text-sm text-muted-foreground">11:30 AM</span>
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium">Bob Johnson</p>
                  <p className="text-xs text-muted-foreground">Lab Results Review</p>
                </div>
                <span className="text-sm text-muted-foreground">2:00 PM</span>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Quick Stats</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">Today's Lab Orders</span>
                <span className="font-medium">{stats?.todayLabOrders || 0}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">Pending Verifications</span>
                <span className="font-medium">{stats?.pendingVerifications || 0}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">Expiring Items</span>
                <span className="font-medium">{stats?.expiringItems || 0}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">Today's Dispensings</span>
                <span className="font-medium">{stats?.todayDispensings || 0}</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

