package com.re.paas.api.infra.database.model;


public class TableDefinition extends BaseTableDefinition {
	
   private java.util.List<LocalSecondaryIndexDefinition> localSecondaryIndexes;
   
   private java.util.List<GlobalSecondaryIndexDefinition> globalSecondaryIndexes;


   public TableDefinition(String tableName) {
       setTableName(tableName);
   }

   public TableDefinition(String tableName, java.util.List<AttributeDefinition> attributeDefinitions, java.util.List<KeySchemaElement> keySchema) {
       setAttributeDefinitions(attributeDefinitions);
       setTableName(tableName);
       setKeySchema(keySchema);
   }

   public TableDefinition withKeySchema(java.util.Collection<KeySchemaElement> keySchema) {
       setKeySchema(keySchema);
       return this;
   }

   public java.util.List<LocalSecondaryIndexDefinition> getLocalSecondaryIndexes() {
       return localSecondaryIndexes;
   }

   public void setLocalSecondaryIndexes(java.util.Collection<LocalSecondaryIndexDefinition> localSecondaryIndexes) {
       if (localSecondaryIndexes == null) {
           this.localSecondaryIndexes = null;
           return;
       }

       this.localSecondaryIndexes = new java.util.ArrayList<LocalSecondaryIndexDefinition>(localSecondaryIndexes);
   }
 
   public TableDefinition withLocalSecondaryIndexes(LocalSecondaryIndexDefinition... localSecondaryIndexes) {
       if (this.localSecondaryIndexes == null) {
           setLocalSecondaryIndexes(new java.util.ArrayList<LocalSecondaryIndexDefinition>(localSecondaryIndexes.length));
       }
       for (LocalSecondaryIndexDefinition ele : localSecondaryIndexes) {
           this.localSecondaryIndexes.add(ele);
       }
       return this;
   }


   public TableDefinition withLocalSecondaryIndexes(java.util.Collection<LocalSecondaryIndexDefinition> localSecondaryIndexes) {
       setLocalSecondaryIndexes(localSecondaryIndexes);
       return this;
   }


   public java.util.List<GlobalSecondaryIndexDefinition> getGlobalSecondaryIndexes() {
       return globalSecondaryIndexes;
   }


   public void setGlobalSecondaryIndexes(java.util.Collection<GlobalSecondaryIndexDefinition> globalSecondaryIndexes) {
       if (globalSecondaryIndexes == null) {
           this.globalSecondaryIndexes = null;
           return;
       }

       this.globalSecondaryIndexes = new java.util.ArrayList<GlobalSecondaryIndexDefinition>(globalSecondaryIndexes);
   }

   public TableDefinition withGlobalSecondaryIndexes(GlobalSecondaryIndexDefinition... globalSecondaryIndexes) {
       if (this.globalSecondaryIndexes == null) {
           setGlobalSecondaryIndexes(new java.util.ArrayList<GlobalSecondaryIndexDefinition>(globalSecondaryIndexes.length));
       }
       for (GlobalSecondaryIndexDefinition ele : globalSecondaryIndexes) {
           this.globalSecondaryIndexes.add(ele);
       }
       return this;
   }

   public TableDefinition withGlobalSecondaryIndexes(java.util.Collection<GlobalSecondaryIndexDefinition> globalSecondaryIndexes) {
       setGlobalSecondaryIndexes(globalSecondaryIndexes);
       return this;
   }
   

   @Override
   public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("{");
       if (getAttributeDefinitions() != null)
           sb.append("AttributeDefinitions: ").append(getAttributeDefinitions()).append(",");
       if (getTableName() != null)
           sb.append("TableName: ").append(getTableName()).append(",");
       if (getKeySchema() != null)
           sb.append("KeySchema: ").append(getKeySchema()).append(",");
       if (getLocalSecondaryIndexes() != null)
           sb.append("LocalSecondaryIndexes: ").append(getLocalSecondaryIndexes()).append(",");
       if (getGlobalSecondaryIndexes() != null)
           sb.append("GlobalSecondaryIndexes: ").append(getGlobalSecondaryIndexes()).append(",");
       sb.append("}");
       return sb.toString();
   }

   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;

       if (obj instanceof TableDefinition == false)
           return false;
       TableDefinition other = (TableDefinition) obj;
       if (other.getAttributeDefinitions() == null ^ this.getAttributeDefinitions() == null)
           return false;
       if (other.getAttributeDefinitions() != null && other.getAttributeDefinitions().equals(this.getAttributeDefinitions()) == false)
           return false;
       if (other.getTableName() == null ^ this.getTableName() == null)
           return false;
       if (other.getTableName() != null && other.getTableName().equals(this.getTableName()) == false)
           return false;
       if (other.getKeySchema() == null ^ this.getKeySchema() == null)
           return false;
       if (other.getKeySchema() != null && other.getKeySchema().equals(this.getKeySchema()) == false)
           return false;
       if (other.getLocalSecondaryIndexes() == null ^ this.getLocalSecondaryIndexes() == null)
           return false;
       if (other.getLocalSecondaryIndexes() != null && other.getLocalSecondaryIndexes().equals(this.getLocalSecondaryIndexes()) == false)
           return false;
       if (other.getGlobalSecondaryIndexes() == null ^ this.getGlobalSecondaryIndexes() == null)
           return false;
       if (other.getGlobalSecondaryIndexes() != null && other.getGlobalSecondaryIndexes().equals(this.getGlobalSecondaryIndexes()) == false)
           return false;
       return true;
   }

   @Override
   public int hashCode() {
       final int prime = 31;
       int hashCode = 1;

       hashCode = prime * hashCode + ((getAttributeDefinitions() == null) ? 0 : getAttributeDefinitions().hashCode());
       hashCode = prime * hashCode + ((getTableName() == null) ? 0 : getTableName().hashCode());
       hashCode = prime * hashCode + ((getKeySchema() == null) ? 0 : getKeySchema().hashCode());
       hashCode = prime * hashCode + ((getLocalSecondaryIndexes() == null) ? 0 : getLocalSecondaryIndexes().hashCode());
       hashCode = prime * hashCode + ((getGlobalSecondaryIndexes() == null) ? 0 : getGlobalSecondaryIndexes().hashCode());
       return hashCode;
   }

}
