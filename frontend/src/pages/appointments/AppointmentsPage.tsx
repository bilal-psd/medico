import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, Calendar, Clock, ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get, PageResponse } from '@/lib/api';
import { formatDateTime, cn } from '@/lib/utils';

interface Appointment {
  id: string;
  patientId: string;
  patientName: string;
  patientMrn: string;
  doctorId: string;
  doctorName: string;
  department: string;
  appointmentDateTime: string;
  endDateTime: string;
  status: string;
  type: string;
  reason: string;
  roomNumber: string;
}

const statusColors: Record<string, string> = {
  SCHEDULED: 'bg-blue-100 text-blue-700',
  CONFIRMED: 'bg-green-100 text-green-700',
  CHECKED_IN: 'bg-purple-100 text-purple-700',
  IN_PROGRESS: 'bg-yellow-100 text-yellow-700',
  COMPLETED: 'bg-gray-100 text-gray-700',
  CANCELLED: 'bg-red-100 text-red-700',
  NO_SHOW: 'bg-orange-100 text-orange-700',
};

export function AppointmentsPage() {
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ['appointments', page],
    queryFn: () => get<PageResponse<Appointment>>(`/appointments?page=${page}&size=10`),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Appointments</h1>
          <p className="text-muted-foreground">
            Manage appointment scheduling and calendar
          </p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          New Appointment
        </Button>
      </div>

      {/* Today's Overview */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-blue-100 p-3">
                <Calendar className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{data?.content.filter(a => a.status === 'SCHEDULED').length || 0}</p>
                <p className="text-sm text-muted-foreground">Scheduled</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-green-100 p-3">
                <Clock className="h-5 w-5 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{data?.content.filter(a => a.status === 'IN_PROGRESS').length || 0}</p>
                <p className="text-sm text-muted-foreground">In Progress</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-gray-100 p-3">
                <Calendar className="h-5 w-5 text-gray-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{data?.content.filter(a => a.status === 'COMPLETED').length || 0}</p>
                <p className="text-sm text-muted-foreground">Completed</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-red-100 p-3">
                <Calendar className="h-5 w-5 text-red-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{data?.content.filter(a => a.status === 'CANCELLED').length || 0}</p>
                <p className="text-sm text-muted-foreground">Cancelled</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Appointments List */}
      <Card>
        <CardHeader>
          <CardTitle>All Appointments</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex h-48 items-center justify-center">
              <LoadingSpinner />
            </div>
          ) : (
            <>
              <div className="rounded-lg border">
                <table className="w-full">
                  <thead>
                    <tr className="border-b bg-muted/50">
                      <th className="p-4 text-left text-sm font-medium">Patient</th>
                      <th className="p-4 text-left text-sm font-medium">Doctor</th>
                      <th className="p-4 text-left text-sm font-medium">Date & Time</th>
                      <th className="p-4 text-left text-sm font-medium">Type</th>
                      <th className="p-4 text-left text-sm font-medium">Status</th>
                      <th className="p-4 text-left text-sm font-medium">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data?.content.map((appointment) => (
                      <tr key={appointment.id} className="border-b last:border-0">
                        <td className="p-4">
                          <div className="font-medium">{appointment.patientName}</div>
                          <div className="text-xs text-muted-foreground font-mono">
                            {appointment.patientMrn}
                          </div>
                        </td>
                        <td className="p-4">
                          <div className="text-sm">{appointment.doctorName}</div>
                          <div className="text-xs text-muted-foreground">
                            {appointment.department}
                          </div>
                        </td>
                        <td className="p-4 text-sm">
                          {formatDateTime(appointment.appointmentDateTime)}
                        </td>
                        <td className="p-4 text-sm capitalize">
                          {appointment.type.toLowerCase().replace('_', ' ')}
                        </td>
                        <td className="p-4">
                          <span
                            className={cn(
                              'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium',
                              statusColors[appointment.status] || 'bg-gray-100 text-gray-700'
                            )}
                          >
                            {appointment.status.replace('_', ' ')}
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

              {/* Pagination */}
              {data && data.totalPages > 1 && (
                <div className="flex items-center justify-between mt-4">
                  <p className="text-sm text-muted-foreground">
                    Showing {page * 10 + 1} to{' '}
                    {Math.min((page + 1) * 10, data.totalElements)} of{' '}
                    {data.totalElements} appointments
                  </p>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(page - 1)}
                      disabled={data.first}
                    >
                      <ChevronLeft className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(page + 1)}
                      disabled={data.last}
                    >
                      <ChevronRight className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

