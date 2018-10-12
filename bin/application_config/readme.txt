		
When testing:
 Implement a suitable LoggingInterceptor to keep track of logs	
				
				
					TODO: Deployment Tests-Cases
			<Most tests should be carried out locally if necessary>
					

 ** All test should run on both linux and windows environment**
 
 -- In both the AWS and Azure SDKs: Does null denote non-value for everything, Does null | "" denote
    non-value for strings? -> Test for both providers
 
 -- Notice how Instance Profile.osPlatform is set from Utils.getPlatform --
 
 -- Notice that in IaasInfrastructure.getInstanceProfile()<AWS>, when testing
    isMapPublicIpOnLaunch(), we describe the master subnet without passing in any filter
    that is associated to isMapPublicIpOnLaunch()
    
    The same also applies when calling isMapPublicIpOnLaunch() on the slave subnet
    --
    
 -- AWSProvider.getInstanceMetaData has to be tested --
 
 
 
 0. Specify clusteringPort that is already in use

1. Specify Wrong Credentials

2. Launch Master Instance, such that it is does have a public ip
3. Launch Master Instance in non-VPC environment:
4. Launch Master Instance in VPC, but supply wrong privateSubnetId

5. For a two or three instance cluster, use the web console to shutdown one of the
   slave instances, to see if out shutdown hook ran, and the node was put out of service
   Even if the partitions stored on this node gets lost due to insufficient memory,
   as far as the master received the "put-out-of-service" request, this test passed
   
6. When creating instances, verify that the service call returns immediately the VM has started 
   its booting sequence (Specifically test in Azure)




#Note
* Virtual Network, VNet, VPC imply the same thing
