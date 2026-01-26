import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { User, Phone, Mail, MapPin, AlertCircle, Calendar, Pill, FlaskConical } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get } from '@/lib/api';
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
  address: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  bloodType: string;
  allergies: string;
  medicalNotes: string;
  active: boolean;
  createdAt: string;
}

export function PatientDetailsPage() {
  const { id } = useParams<{ id: string }>();

  const { data: patient, isLoading } = useQuery({
    queryKey: ['patient', id],
    queryFn: () => get<Patient>(`/patients/${id}`),
  });

  if (isLoading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!patient) {
    return <div>Patient not found</div>;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-4">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary text-primary-foreground text-xl font-bold">
            {patient.firstName[0]}
            {patient.lastName[0]}
          </div>
          <div>
            <h1 className="text-3xl font-bold tracking-tight">
              {patient.firstName} {patient.lastName}
            </h1>
            <p className="text-muted-foreground font-mono">
              {patient.medicalRecordNumber}
            </p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="outline">Edit Patient</Button>
          <Button>New Appointment</Button>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Personal Information */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              Personal Information
            </CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="text-sm text-muted-foreground">Date of Birth</label>
              <p className="font-medium">{formatDate(patient.dateOfBirth)}</p>
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Gender</label>
              <p className="font-medium capitalize">{patient.gender.toLowerCase()}</p>
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Blood Type</label>
              <p className="font-medium">{patient.bloodType?.replace('_', ' ') || 'Not recorded'}</p>
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Status</label>
              <p>
                <span
                  className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                    patient.active
                      ? 'bg-green-100 text-green-700'
                      : 'bg-red-100 text-red-700'
                  }`}
                >
                  {patient.active ? 'Active' : 'Inactive'}
                </span>
              </p>
            </div>
          </CardContent>
        </Card>

        {/* Contact Information */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Phone className="h-5 w-5" />
              Contact
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center gap-3">
              <Phone className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm">{patient.phoneNumber || 'Not provided'}</span>
            </div>
            <div className="flex items-center gap-3">
              <Mail className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm">{patient.email || 'Not provided'}</span>
            </div>
            <div className="flex items-start gap-3">
              <MapPin className="h-4 w-4 text-muted-foreground mt-0.5" />
              <span className="text-sm">
                {patient.address ? (
                  <>
                    {patient.address}
                    <br />
                    {patient.city}, {patient.state} {patient.postalCode}
                    <br />
                    {patient.country}
                  </>
                ) : (
                  'Not provided'
                )}
              </span>
            </div>
          </CardContent>
        </Card>

        {/* Emergency Contact */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertCircle className="h-5 w-5" />
              Emergency Contact
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            <div>
              <label className="text-sm text-muted-foreground">Name</label>
              <p className="font-medium">{patient.emergencyContactName || 'Not provided'}</p>
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Phone</label>
              <p className="font-medium">{patient.emergencyContactPhone || 'Not provided'}</p>
            </div>
          </CardContent>
        </Card>

        {/* Medical Notes */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle>Medical Notes & Allergies</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <label className="text-sm text-muted-foreground">Allergies</label>
              <p className="mt-1 text-sm">{patient.allergies || 'None recorded'}</p>
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Medical Notes</label>
              <p className="mt-1 text-sm">{patient.medicalNotes || 'No notes'}</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <div className="grid gap-4 sm:grid-cols-3">
        <Card className="card-hover cursor-pointer">
          <CardContent className="flex items-center gap-4 p-6">
            <div className="rounded-lg bg-blue-100 p-3">
              <Calendar className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <p className="font-medium">Appointments</p>
              <p className="text-sm text-muted-foreground">View history</p>
            </div>
          </CardContent>
        </Card>
        <Card className="card-hover cursor-pointer">
          <CardContent className="flex items-center gap-4 p-6">
            <div className="rounded-lg bg-purple-100 p-3">
              <Pill className="h-6 w-6 text-purple-600" />
            </div>
            <div>
              <p className="font-medium">Prescriptions</p>
              <p className="text-sm text-muted-foreground">View all</p>
            </div>
          </CardContent>
        </Card>
        <Card className="card-hover cursor-pointer">
          <CardContent className="flex items-center gap-4 p-6">
            <div className="rounded-lg bg-orange-100 p-3">
              <FlaskConical className="h-6 w-6 text-orange-600" />
            </div>
            <div>
              <p className="font-medium">Lab Results</p>
              <p className="text-sm text-muted-foreground">View reports</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

