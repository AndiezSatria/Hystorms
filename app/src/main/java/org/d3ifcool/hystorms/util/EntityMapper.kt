package org.d3ifcool.hystorms.util

interface EntityMapper<Entity, DomainModel> {
    fun mapFromEntity(entity: Entity): DomainModel
    fun mapFromDomain(domain: DomainModel): Entity
}