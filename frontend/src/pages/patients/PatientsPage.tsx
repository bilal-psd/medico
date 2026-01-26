import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Plus, Search, ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get, PageResponse } from '@/lib/api';
import { formatDate } from '@/lib/utils';

interface Patient {
  id: string;
  medicalRecordNumber: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phoneNumber: string;
  email: string;
  active: boolean;
}

export function PatientsPage() {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ['patients', page, search],
    queryFn: () =>
      search
        ? get<PageResponse<Patient>>(`/patients/search?query=${search}&page=${page}&size=10`)
        : get<PageResponse<Patient>>(`/patients?page=${page}&size=10`),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Patients</h1>
          <p className="text-muted-foreground">
            Manage patient registrations and records
          </p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          Add Patient
        </Button>
      </div>

      {/* Search */}
      <Card>
        <CardContent className="pt-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Search by name, MRN, phone, or email..."
              className="pl-10"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </CardContent>
      </Card>

      {/* Patients Table */}
      <Card>
        <CardHeader>
          <CardTitle>Patient List</CardTitle>
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
                      <th className="p-4 text-left text-sm font-medium">MRN</th>
                      <th className="p-4 text-left text-sm font-medium">Name</th>
                      <th className="p-4 text-left text-sm font-medium">Date of Birth</th>
                      <th className="p-4 text-left text-sm font-medium">Gender</th>
                      <th className="p-4 text-left text-sm font-medium">Contact</th>
                      <th className="p-4 text-left text-sm font-medium">Status</th>
                      <th className="p-4 text-left text-sm font-medium">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data?.content.map((patient) => (
                      <tr key={patient.id} className="border-b last:border-0">
                        <td className="p-4 text-sm font-mono">
                          {patient.medicalRecordNumber}
                        </td>
                        <td className="p-4 text-sm font-medium">
                          {patient.firstName} {patient.lastName}
                        </td>
                        <td className="p-4 text-sm">
                          {formatDate(patient.dateOfBirth)}
                        </td>
                        <td className="p-4 text-sm capitalize">
                          {patient.gender.toLowerCase()}
                        </td>
                        <td className="p-4 text-sm">
                          <div>{patient.phoneNumber}</div>
                          <div className="text-xs text-muted-foreground">
                            {patient.email}
                          </div>
                        </td>
                        <td className="p-4">
                          <span
                            className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                              patient.active
                                ? 'bg-green-100 text-green-700'
                                : 'bg-red-100 text-red-700'
                            }`}
                          >
                            {patient.active ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="p-4">
                          <Link to={`/patients/${patient.id}`}>
                            <Button variant="outline" size="sm">
                              View
                            </Button>
                          </Link>
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
                    {data.totalElements} patients
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

