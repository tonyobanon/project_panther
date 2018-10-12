$computerSystemProduct = Get-WmiObject -class Win32_ComputerSystemProduct -namespace root\CIMV2
'{0}' -f $computerSystemProduct.UUID