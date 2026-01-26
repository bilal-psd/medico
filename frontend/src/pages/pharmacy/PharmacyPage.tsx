import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, Search, Pill, Package, AlertTriangle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get, PageResponse } from '@/lib/api';
import { formatCurrency } from '@/lib/utils';

interface Medication {
  id: string;
  code: string;
  name: string;
  genericName: string;
  brandName: string;
  category: string;
  form: string;
  strength: string;
  unit: string;
  manufacturer: string;
  unitPrice: number;
  reorderLevel: number;
  requiresPrescription: boolean;
  controlledSubstance: boolean;
  active: boolean;
}

export function PharmacyPage() {
  const [search, setSearch] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['medications', search],
    queryFn: () =>
      search
        ? get<PageResponse<Medication>>(`/medications/search?query=${search}&size=20`)
        : get<PageResponse<Medication>>('/medications?size=20'),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Pharmacy</h1>
          <p className="text-muted-foreground">
            Manage medications and prescriptions
          </p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          Add Medication
        </Button>
      </div>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-purple-100 p-3">
                <Pill className="h-5 w-5 text-purple-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{data?.totalElements || 0}</p>
                <p className="text-sm text-muted-foreground">Total Medications</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-orange-100 p-3">
                <Package className="h-5 w-5 text-orange-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">12</p>
                <p className="text-sm text-muted-foreground">Low Stock Items</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-red-100 p-3">
                <AlertTriangle className="h-5 w-5 text-red-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">5</p>
                <p className="text-sm text-muted-foreground">Expiring Soon</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Search */}
      <Card>
        <CardContent className="pt-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Search medications by name or code..."
              className="pl-10"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </CardContent>
      </Card>

      {/* Medications Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {isLoading ? (
          <div className="col-span-full flex h-48 items-center justify-center">
            <LoadingSpinner />
          </div>
        ) : (
          data?.content.map((medication) => (
            <Card key={medication.id} className="card-hover">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle className="text-base">{medication.name}</CardTitle>
                    <p className="text-xs text-muted-foreground font-mono">
                      {medication.code}
                    </p>
                  </div>
                  <span className="inline-flex items-center rounded-full px-2 py-1 text-xs font-medium bg-primary/10 text-primary">
                    {medication.form}
                  </span>
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="grid grid-cols-2 gap-2 text-sm">
                  <div>
                    <p className="text-muted-foreground">Generic</p>
                    <p className="font-medium">{medication.genericName || '-'}</p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Strength</p>
                    <p className="font-medium">{medication.strength || '-'}</p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Category</p>
                    <p className="font-medium capitalize">
                      {medication.category.toLowerCase().replace('_', ' ')}
                    </p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Price</p>
                    <p className="font-medium">{formatCurrency(medication.unitPrice || 0)}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {medication.requiresPrescription && (
                    <span className="inline-flex items-center rounded-full px-2 py-1 text-xs font-medium bg-blue-100 text-blue-700">
                      Rx Required
                    </span>
                  )}
                  {medication.controlledSubstance && (
                    <span className="inline-flex items-center rounded-full px-2 py-1 text-xs font-medium bg-red-100 text-red-700">
                      Controlled
                    </span>
                  )}
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>
    </div>
  );
}

