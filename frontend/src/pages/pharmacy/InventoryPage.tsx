import { useQuery } from '@tanstack/react-query';
import { Plus, AlertTriangle, Calendar } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { get, PageResponse } from '@/lib/api';
import { formatDate, cn } from '@/lib/utils';

interface Inventory {
  id: string;
  medicationId: string;
  medicationName: string;
  medicationCode: string;
  batchNumber: string;
  quantity: number;
  reservedQuantity: number;
  availableQuantity: number;
  expiryDate: string;
  manufactureDate: string;
  location: string;
  status: string;
  supplierName: string;
  expired: boolean;
  expiringSoon: boolean;
}

interface InventoryAlert {
  inventoryId: string;
  medicationId: string;
  medicationName: string;
  medicationCode: string;
  batchNumber: string;
  alertType: string;
  message: string;
  currentQuantity: number;
  reorderLevel: number;
  expiryDate: string;
}

const statusColors: Record<string, string> = {
  AVAILABLE: 'bg-green-100 text-green-700',
  LOW_STOCK: 'bg-yellow-100 text-yellow-700',
  OUT_OF_STOCK: 'bg-red-100 text-red-700',
  EXPIRED: 'bg-gray-100 text-gray-700',
  RESERVED: 'bg-blue-100 text-blue-700',
};

export function InventoryPage() {
  const { data: inventory, isLoading } = useQuery({
    queryKey: ['inventory'],
    queryFn: () => get<PageResponse<Inventory>>('/inventory?size=50'),
  });

  const { data: alerts } = useQuery({
    queryKey: ['inventory-alerts'],
    queryFn: () => get<InventoryAlert[]>('/inventory/alerts'),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Inventory</h1>
          <p className="text-muted-foreground">
            Track stock levels and manage inventory
          </p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          Add Stock
        </Button>
      </div>

      {/* Alerts */}
      {alerts && alerts.length > 0 && (
        <Card className="border-orange-200 bg-orange-50">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-orange-700">
              <AlertTriangle className="h-5 w-5" />
              Inventory Alerts ({alerts.length})
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              {alerts.slice(0, 5).map((alert) => (
                <div
                  key={alert.inventoryId}
                  className="flex items-center justify-between rounded-lg bg-white p-3"
                >
                  <div>
                    <p className="font-medium">{alert.medicationName}</p>
                    <p className="text-sm text-muted-foreground">{alert.message}</p>
                  </div>
                  <span
                    className={cn(
                      'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium',
                      alert.alertType === 'LOW_STOCK'
                        ? 'bg-yellow-100 text-yellow-700'
                        : alert.alertType === 'EXPIRED' || alert.alertType === 'EXPIRING_SOON'
                        ? 'bg-red-100 text-red-700'
                        : 'bg-gray-100 text-gray-700'
                    )}
                  >
                    {alert.alertType.replace('_', ' ')}
                  </span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Inventory Table */}
      <Card>
        <CardHeader>
          <CardTitle>Current Stock</CardTitle>
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
                    <th className="p-4 text-left text-sm font-medium">Medication</th>
                    <th className="p-4 text-left text-sm font-medium">Batch</th>
                    <th className="p-4 text-left text-sm font-medium">Quantity</th>
                    <th className="p-4 text-left text-sm font-medium">Expiry Date</th>
                    <th className="p-4 text-left text-sm font-medium">Location</th>
                    <th className="p-4 text-left text-sm font-medium">Status</th>
                    <th className="p-4 text-left text-sm font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {inventory?.content.map((item) => (
                    <tr key={item.id} className="border-b last:border-0">
                      <td className="p-4">
                        <div className="font-medium">{item.medicationName}</div>
                        <div className="text-xs text-muted-foreground font-mono">
                          {item.medicationCode}
                        </div>
                      </td>
                      <td className="p-4 text-sm font-mono">{item.batchNumber}</td>
                      <td className="p-4">
                        <div className="text-sm font-medium">{item.availableQuantity}</div>
                        {item.reservedQuantity > 0 && (
                          <div className="text-xs text-muted-foreground">
                            ({item.reservedQuantity} reserved)
                          </div>
                        )}
                      </td>
                      <td className="p-4">
                        <div className="flex items-center gap-2">
                          <Calendar className="h-4 w-4 text-muted-foreground" />
                          <span
                            className={cn(
                              'text-sm',
                              item.expired
                                ? 'text-red-600 font-medium'
                                : item.expiringSoon
                                ? 'text-orange-600'
                                : ''
                            )}
                          >
                            {formatDate(item.expiryDate)}
                          </span>
                        </div>
                      </td>
                      <td className="p-4 text-sm">{item.location || '-'}</td>
                      <td className="p-4">
                        <span
                          className={cn(
                            'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium',
                            statusColors[item.status] || 'bg-gray-100 text-gray-700'
                          )}
                        >
                          {item.status.replace('_', ' ')}
                        </span>
                      </td>
                      <td className="p-4">
                        <Button variant="outline" size="sm">
                          Adjust
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

