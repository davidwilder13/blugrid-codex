export const KotlinCrudServiceImplTemplate = ({
                                                  packageName,
                                                  entityName,
                                              }: {
    packageName: string
    entityName: string
}) => `package ${packageName}.service

import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import jakarta.transaction.Transactional.TxType.REQUIRES_NEW
import ${packageName}.repository.${entityName}Repository
import ${packageName}.mapper.${entityName}Mapper
import ${packageName}.repository.model.${entityName}Entity
import net.blugrid.api.common.model.${entityName.toLowerCase()}.${entityName}
import net.blugrid.api.common.model.${entityName.toLowerCase()}.${entityName}Create
import net.blugrid.api.common.model.${entityName.toLowerCase()}.${entityName}Update
import net.blugrid.api.common.service.GenericCrudServiceImpl

@Singleton
open class ${entityName}StateServiceImpl(
    private val repository: ${entityName}Repository,
    private val mapper: ${entityName}Mapper
) : GenericCrudServiceImpl<${entityName}, ${entityName}Create, ${entityName}Update, ${entityName}Entity, ${entityName}Mapper>(repository, mapper),
    ${entityName}StateService {

    @Transactional(value = REQUIRES_NEW)
    override fun create(newResource: ${entityName}Create): ${entityName} {
        return repository.saveAndFlush(newResource.toEntity())
            .toResponse()
    }
}
`
