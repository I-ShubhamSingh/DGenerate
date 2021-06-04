package com.easemywork.model;

import java.util.Objects;

public class Datasource {

	private String name;
    private String entityPackage;
    private String repositoryPackage;
    private String persistentUnit;
    private String isPrimaryDatasource;

    public Datasource(){}

    public Datasource(String name) {
        this.name = name;
    }

    public Datasource(String name, String entityPackage, String repositoryPackage, String persistentUnit,
			String isPrimaryDatasource) {
		super();
		this.name = name;
		this.entityPackage = entityPackage;
		this.repositoryPackage = repositoryPackage;
		this.persistentUnit = persistentUnit;
		this.isPrimaryDatasource = isPrimaryDatasource;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    public void setRepositoryPackage(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
    }

    public String getPersistentUnit() {
        return persistentUnit;
    }

    public void setPersistentUnit(String persistentUnit) {
        this.persistentUnit = persistentUnit;
    }

    public String getIsPrimaryDatasource() {
        return isPrimaryDatasource;
    }

    public void setIsPrimaryDatasource(String isPrimaryDatasource) {
        this.isPrimaryDatasource = isPrimaryDatasource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Datasource that = (Datasource) o;
        return name.equals(that.name) && entityPackage.equals(that.entityPackage) && repositoryPackage.equals(that.repositoryPackage) && persistentUnit.equals(that.persistentUnit) && isPrimaryDatasource.equals(that.isPrimaryDatasource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, entityPackage, repositoryPackage, persistentUnit, isPrimaryDatasource);
    }

    @Override
    public String toString() {
        return "Datasource{" +
                "name='" + name + '\'' +
                ", entityPackage='" + entityPackage + '\'' +
                ", repositoryPackage='" + repositoryPackage + '\'' +
                ", persistentUnit='" + persistentUnit + '\'' +
                ", isPrimaryDatasource='" + isPrimaryDatasource + '\'' +
                '}';
    }

}
