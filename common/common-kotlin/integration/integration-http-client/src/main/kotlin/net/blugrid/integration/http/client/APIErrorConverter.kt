package net.blugrid.integration.http.client

import net.blugrid.common.api.exception.BusinessRuleViolationAPIException
import net.blugrid.common.api.exception.InternalServerErrorAPIException
import net.blugrid.common.api.exception.ResourceAccessDeniedAPIException
import net.blugrid.common.api.exception.ResourceAlreadyExistsAPIException
import net.blugrid.common.api.exception.ResourceConflictAPIException
import net.blugrid.common.api.exception.ResourceNotFoundAPIException
import net.blugrid.common.api.exception.ResourceValidationAPIException
import net.blugrid.common.api.exception.TenantContextMissingAPIException
import net.blugrid.common.model.exception.APIError
import net.blugrid.common.model.exception.APIException

object APIErrorConverter {
    fun convert(error: APIError): APIException? =
        when (error.type) {
            ResourceNotFoundAPIException.CODE -> ResourceNotFoundAPIException(error)
            ResourceAlreadyExistsAPIException.CODE -> ResourceAlreadyExistsAPIException(error)
            ResourceValidationAPIException.CODE -> ResourceValidationAPIException(error)
            TenantContextMissingAPIException.CODE -> TenantContextMissingAPIException(error)
            ResourceAccessDeniedAPIException.CODE -> ResourceAccessDeniedAPIException(error)
            BusinessRuleViolationAPIException.CODE -> BusinessRuleViolationAPIException(error)
            ResourceConflictAPIException.CODE -> ResourceConflictAPIException(error)
            InternalServerErrorAPIException.CODE -> InternalServerErrorAPIException(error)
            else -> null
        }
}
