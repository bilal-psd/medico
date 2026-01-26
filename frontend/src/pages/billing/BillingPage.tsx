import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, DollarSign, Clock, AlertTriangle, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get, PageResponse } from '@/lib/api';
import { formatDateTime, formatCurrency, cn } from '@/lib/utils';

interface Invoice {
  id: string;
  invoiceNumber: string;
  patientId: string;
  patientName: string;
  patientMrn: string;
  invoiceDate: string;
  dueDate: string;
  status: string;
  totalAmount: number;
  paidAmount: number;
  balanceDue: number;
}

interface FinancialSummary {
  totalRevenue: number;
  totalCollected: number;
  totalOutstanding: number;
  pendingInvoices: number;
  overdueInvoices: number;
  todayInvoices: number;
  todayPayments: number;
}

const statusColors: Record<string, string> = {
  DRAFT: 'bg-gray-100 text-gray-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  PARTIALLY_PAID: 'bg-blue-100 text-blue-700',
  PAID: 'bg-green-100 text-green-700',
  OVERDUE: 'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-700',
};

export function BillingPage() {
  const [statusFilter, setStatusFilter] = useState<string>('');

  const { data: invoices, isLoading } = useQuery({
    queryKey: ['invoices', statusFilter],
    queryFn: () =>
      statusFilter
        ? get<PageResponse<Invoice>>(`/invoices/status/${statusFilter}?size=20`)
        : get<PageResponse<Invoice>>('/invoices?size=20'),
  });

  const { data: summary } = useQuery({
    queryKey: ['financial-summary'],
    queryFn: () => get<FinancialSummary>('/reports/financial/summary'),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Billing & Finance</h1>
          <p className="text-muted-foreground">
            Manage invoices and payments
          </p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          New Invoice
        </Button>
      </div>

      {/* Financial Summary */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-green-100 p-3">
                <DollarSign className="h-5 w-5 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {formatCurrency(summary?.totalRevenue || 0)}
                </p>
                <p className="text-sm text-muted-foreground">Monthly Revenue</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-blue-100 p-3">
                <CheckCircle className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {formatCurrency(summary?.totalCollected || 0)}
                </p>
                <p className="text-sm text-muted-foreground">Collected</p>
              </div>
            </div>
          </CardContent>
        </Card>
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
                <p className="text-2xl font-bold">{summary?.pendingInvoices || 0}</p>
                <p className="text-sm text-muted-foreground">Pending</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card
          className={cn('cursor-pointer card-hover', statusFilter === 'OVERDUE' && 'ring-2 ring-primary')}
          onClick={() => setStatusFilter(statusFilter === 'OVERDUE' ? '' : 'OVERDUE')}
        >
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-red-100 p-3">
                <AlertTriangle className="h-5 w-5 text-red-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{summary?.overdueInvoices || 0}</p>
                <p className="text-sm text-muted-foreground">Overdue</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Outstanding Balance Alert */}
      {(summary?.totalOutstanding || 0) > 0 && (
        <Card className="border-orange-200 bg-orange-50">
          <CardContent className="flex items-center justify-between py-4">
            <div className="flex items-center gap-3">
              <AlertTriangle className="h-5 w-5 text-orange-600" />
              <div>
                <p className="font-medium text-orange-900">Outstanding Balance</p>
                <p className="text-sm text-orange-700">
                  Total amount due from all pending and overdue invoices
                </p>
              </div>
            </div>
            <p className="text-2xl font-bold text-orange-900">
              {formatCurrency(summary?.totalOutstanding || 0)}
            </p>
          </CardContent>
        </Card>
      )}

      {/* Invoices Table */}
      <Card>
        <CardHeader>
          <CardTitle>Invoices</CardTitle>
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
                    <th className="p-4 text-left text-sm font-medium">Invoice #</th>
                    <th className="p-4 text-left text-sm font-medium">Patient</th>
                    <th className="p-4 text-left text-sm font-medium">Date</th>
                    <th className="p-4 text-left text-sm font-medium">Total</th>
                    <th className="p-4 text-left text-sm font-medium">Paid</th>
                    <th className="p-4 text-left text-sm font-medium">Balance</th>
                    <th className="p-4 text-left text-sm font-medium">Status</th>
                    <th className="p-4 text-left text-sm font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {invoices?.content.map((invoice) => (
                    <tr key={invoice.id} className="border-b last:border-0">
                      <td className="p-4 text-sm font-mono">{invoice.invoiceNumber}</td>
                      <td className="p-4">
                        <div className="font-medium">{invoice.patientName}</div>
                        <div className="text-xs text-muted-foreground font-mono">
                          {invoice.patientMrn}
                        </div>
                      </td>
                      <td className="p-4 text-sm">{formatDateTime(invoice.invoiceDate)}</td>
                      <td className="p-4 text-sm font-medium">
                        {formatCurrency(invoice.totalAmount)}
                      </td>
                      <td className="p-4 text-sm text-green-600">
                        {formatCurrency(invoice.paidAmount)}
                      </td>
                      <td className="p-4 text-sm font-medium text-orange-600">
                        {formatCurrency(invoice.balanceDue)}
                      </td>
                      <td className="p-4">
                        <span
                          className={cn(
                            'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium',
                            statusColors[invoice.status] || 'bg-gray-100 text-gray-700'
                          )}
                        >
                          {invoice.status.replace('_', ' ')}
                        </span>
                      </td>
                      <td className="p-4 space-x-2">
                        <Button variant="outline" size="sm">
                          View
                        </Button>
                        {invoice.balanceDue > 0 && (
                          <Button size="sm">Pay</Button>
                        )}
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

