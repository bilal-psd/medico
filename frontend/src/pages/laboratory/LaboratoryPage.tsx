import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, FlaskConical, Clock, CheckCircle, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get, PageResponse } from '@/lib/api';
import { formatDateTime, cn } from '@/lib/utils';

interface LabOrder {
  id: string;
  orderNumber: string;
  patientId: string;
  patientName: string;
  patientMrn: string;
  orderingDoctorName: string;
  orderDate: string;
  priority: string;
  status: string;
  clinicalNotes: string;
}

const statusColors: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-700',
  SAMPLE_COLLECTED: 'bg-blue-100 text-blue-700',
  IN_PROGRESS: 'bg-purple-100 text-purple-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
};

const priorityColors: Record<string, string> = {
  ROUTINE: 'bg-gray-100 text-gray-700',
  URGENT: 'bg-orange-100 text-orange-700',
  STAT: 'bg-red-100 text-red-700',
};

export function LaboratoryPage() {
  const [statusFilter, setStatusFilter] = useState<string>('');

  const { data, isLoading } = useQuery({
    queryKey: ['lab-orders', statusFilter],
    queryFn: () =>
      statusFilter
        ? get<PageResponse<LabOrder>>(`/lab-orders/status/${statusFilter}?size=20`)
        : get<PageResponse<LabOrder>>('/lab-orders?size=20'),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Laboratory</h1>
          <p className="text-muted-foreground">
            Manage lab orders and results
          </p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          New Lab Order
        </Button>
      </div>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card
          className={cn('cursor-pointer card-hover', statusFilter === 'PENDING' && 'ring-2 ring-primary')}
          onClick={() => setStatusFilter(statusFilter === 'PENDING' ? '' : 'PENDING')}
        >
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-yellow-100 p-3">
                <Clock className="h-5 w-5 text-yellow-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {data?.content.filter((o) => o.status === 'PENDING').length || 0}
                </p>
                <p className="text-sm text-muted-foreground">Pending</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card
          className={cn('cursor-pointer card-hover', statusFilter === 'IN_PROGRESS' && 'ring-2 ring-primary')}
          onClick={() => setStatusFilter(statusFilter === 'IN_PROGRESS' ? '' : 'IN_PROGRESS')}
        >
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-purple-100 p-3">
                <FlaskConical className="h-5 w-5 text-purple-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {data?.content.filter((o) => o.status === 'IN_PROGRESS').length || 0}
                </p>
                <p className="text-sm text-muted-foreground">In Progress</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card
          className={cn('cursor-pointer card-hover', statusFilter === 'COMPLETED' && 'ring-2 ring-primary')}
          onClick={() => setStatusFilter(statusFilter === 'COMPLETED' ? '' : 'COMPLETED')}
        >
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-green-100 p-3">
                <CheckCircle className="h-5 w-5 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {data?.content.filter((o) => o.status === 'COMPLETED').length || 0}
                </p>
                <p className="text-sm text-muted-foreground">Completed</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card className="cursor-pointer card-hover">
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-red-100 p-3">
                <AlertCircle className="h-5 w-5 text-red-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">3</p>
                <p className="text-sm text-muted-foreground">Critical Results</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Lab Orders Table */}
      <Card>
        <CardHeader>
          <CardTitle>Lab Orders</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex h-48 items-center justify-center">
              <LoadingSpinner />
            </div>
          ) : (
            <div className="rounded-lg border">
              <table className="w-full">
                <thead>
                  <tr className="border-b bg-muted/50">
                    <th className="p-4 text-left text-sm font-medium">Order #</th>
                    <th className="p-4 text-left text-sm font-medium">Patient</th>
                    <th className="p-4 text-left text-sm font-medium">Ordered By</th>
                    <th className="p-4 text-left text-sm font-medium">Date</th>
                    <th className="p-4 text-left text-sm font-medium">Priority</th>
                    <th className="p-4 text-left text-sm font-medium">Status</th>
                    <th className="p-4 text-left text-sm font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.content.map((order) => (
                    <tr key={order.id} className="border-b last:border-0">
                      <td className="p-4 text-sm font-mono">{order.orderNumber}</td>
                      <td className="p-4">
                        <div className="font-medium">{order.patientName}</div>
                        <div className="text-xs text-muted-foreground font-mono">
                          {order.patientMrn}
                        </div>
                      </td>
                      <td className="p-4 text-sm">{order.orderingDoctorName}</td>
                      <td className="p-4 text-sm">{formatDateTime(order.orderDate)}</td>
                      <td className="p-4">
                        <span
                          className={cn(
                            'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium',
                            priorityColors[order.priority] || 'bg-gray-100 text-gray-700'
                          )}
                        >
                          {order.priority}
                        </span>
                      </td>
                      <td className="p-4">
                        <span
                          className={cn(
                            'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium',
                            statusColors[order.status] || 'bg-gray-100 text-gray-700'
                          )}
                        >
                          {order.status.replace('_', ' ')}
                        </span>
                      </td>
                      <td className="p-4">
                        <Button variant="outline" size="sm">
                          View
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

