package net.blugrid.api.common.model.resource

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Resource type")
enum class ResourceType {
    @JsonProperty("BRAND")
    @Schema(description = "Represents a brand resource type.")
    BRAND,

    @JsonProperty("LOGO")
    @Schema(description = "Represents a logo resource type.")
    LOGO,

    @JsonProperty("BOOK")
    @Schema(description = "Represents a book resource type.", example = "BOOK")
    BOOK,

    @JsonProperty("RESOURCE")
    @Schema(description = "Represents a generic resource type.")
    RESOURCE,

    @JsonProperty("COMMAND")
    @Schema(description = "Represents a command resource type.")
    COMMAND,

    @JsonProperty("CALENDAR")
    @Schema(description = "Represents a calendar resource type.")
    CALENDAR,

    @JsonProperty("CALENDAR_LEVEL")
    @Schema(description = "Represents a calendar level resource type.")
    CALENDAR_LEVEL,

    @JsonProperty("BUSINESS_UNIT")
    @Schema(description = "Represents a business unit resource type.")
    BUSINESS_UNIT,

    @JsonProperty("ORGANISATION")
    @Schema(description = "Represents an organisation resource type.")
    ORGANISATION,

    @JsonProperty("DEVICE")
    @Schema(description = "Represents a device resource type.")
    DEVICE,

    @JsonProperty("OPERATIONAL_PARTY")
    @Schema(description = "Represents an operational party resource type.")
    OPERATIONAL_PARTY,

    @JsonProperty("OPERATOR")
    @Schema(description = "Represents an operator resource type.")
    OPERATOR,

    @JsonProperty("BUSINESS_UNIT_MEMBERSHIP")
    @Schema(description = "Represents a business unit membership resource type.")
    BUSINESS_UNIT_MEMBERSHIP,

    @JsonProperty("USER_IDENTITY")
    @Schema(description = "Represents a user identity resource type.")
    USER_IDENTITY,

    @JsonProperty("WORKER")
    @Schema(description = "Represents a worker resource type.")
    WORKER,

    @JsonProperty("SESSION")
    @Schema(description = "Represents a session resource type.")
    SESSION,

    @JsonProperty("PARTY_REGISTRATION")
    @Schema(description = "Represents a party registration resource type.")
    PARTY_REGISTRATION,

    @JsonProperty("PARTY_REGISTRATION_INVITATION")
    @Schema(description = "Represents a party registration invitation resource type.")
    PARTY_REGISTRATION_INVITATION,

    @JsonProperty("ANONYMOUS_PARTY")
    @Schema(description = "Represents an anonymous party resource type.")
    ANONYMOUS_PARTY,

    @JsonProperty("BUSINESS")
    @Schema(description = "Represents a business resource type.")
    BUSINESS,

    @JsonProperty("PERSON")
    @Schema(description = "Represents a person resource type.")
    PERSON,

    @JsonProperty("ADDRESS")
    @Schema(description = "Represents an address resource type.")
    ADDRESS,

    @JsonProperty("EMAIL_ADDRESS")
    @Schema(description = "Represents an email address resource type.")
    EMAIL_ADDRESS,

    @JsonProperty("TELEPHONE")
    @Schema(description = "Represents a telephone resource type.")
    TELEPHONE,

    @JsonProperty("ORGANISATION_MEMBERSHIP")
    @Schema(description = "Represents an organisation membership resource type.")
    ORGANISATION_MEMBERSHIP,

    @JsonProperty("PARTY_CONTACT_METHOD")
    @Schema(description = "Represents a party contact method resource type.")
    PARTY_CONTACT_METHOD,

    @JsonProperty("EMAIL")
    @Schema(description = "Represents an email resource type.")
    EMAIL,

    @JsonProperty("EMAIL_SERVICE")
    @Schema(description = "Represents an email service resource type.")
    EMAIL_SERVICE,

    @JsonProperty("FINANCIAL_ACCOUNT")
    @Schema(description = "Represents a financial account resource type.")
    FINANCIAL_ACCOUNT,

    @JsonProperty("FINANCIAL_GOAL")
    @Schema(description = "Represents a financial goal resource type.")
    FINANCIAL_GOAL,

    @JsonProperty("FINANCIAL_GOAL_BANK_ACCOUNT")
    @Schema(description = "Represents a financial goal bank account resource type.")
    FINANCIAL_GOAL_BANK_ACCOUNT,

    @JsonProperty("FINANCIAL_GOAL_LOAN")
    @Schema(description = "Represents a financial goal loan resource type.")
    FINANCIAL_GOAL_LOAN,

    @JsonProperty("FINANCIAL_GOAL_CAPITAL_PURCHASE")
    @Schema(description = "Represents a financial goal capital purchase resource type.")
    FINANCIAL_GOAL_CAPITAL_PURCHASE,

    @JsonProperty("FINANCIAL_GOAL_REPORT_PERIOD")
    @Schema(description = "Represents a financial goal report period resource type.")
    FINANCIAL_GOAL_REPORT_PERIOD,

    @JsonProperty("FINANCIAL_GOAL_YEAR")
    @Schema(description = "Represents a financial goal year resource type.")
    FINANCIAL_GOAL_YEAR,

    @JsonProperty("FINANCIAL_GOAL_YEAR_EXPENSE")
    @Schema(description = "Represents a financial goal year expense resource type.")
    FINANCIAL_GOAL_YEAR_EXPENSE,

    @JsonProperty("FINANCIAL_GOAL_YEAR_SALES")
    @Schema(description = "Represents a financial goal year sales resource type.")
    FINANCIAL_GOAL_YEAR_SALES,

    @JsonProperty("FINANCIAL_GOAL_YEAR_WAGE")
    @Schema(description = "Represents a financial goal year wage resource type.")
    FINANCIAL_GOAL_YEAR_WAGE,

    @JsonProperty("FINANCIAL_LEDGER_SUMMARY")
    @Schema(description = "Represents a financial ledger summary resource type.")
    FINANCIAL_LEDGER_SUMMARY,

    @JsonProperty("LOAN_AGREEMENT")
    @Schema(description = "Represents a loan agreement resource type.")
    LOAN_AGREEMENT,

    @JsonProperty("LOAN_AMORTIZATION_SCHEDULE")
    @Schema(description = "Represents a loan amortization schedule resource type.")
    LOAN_AMORTIZATION_SCHEDULE,

    @JsonProperty("LOAN_AMORTIZATION_PAYMENT")
    @Schema(description = "Represents a loan amortization payment resource type.")
    LOAN_AMORTIZATION_PAYMENT,

    @JsonProperty("JOB_POSITION")
    @Schema(description = "Represents a job position resource type.")
    JOB_POSITION,

    @JsonProperty("SALES_SUMMARY")
    @Schema(description = "Represents a sales summary resource type.")
    SALES_SUMMARY,

    @JsonProperty("BUSINESS_STRATEGY")
    @Schema(description = "Represents a business strategy resource type.")
    BUSINESS_STRATEGY,

    @JsonProperty("BUSINESS_STRATEGY_ACTION")
    @Schema(description = "Represents a business strategy action resource type.")
    BUSINESS_STRATEGY_ACTION,

    @JsonProperty("BUSINESS_STRATEGY_ISSUE")
    @Schema(description = "Represents a business strategy issue resource type.")
    BUSINESS_STRATEGY_ISSUE,

    @JsonProperty("BUSINESS_STRATEGY_GOAL")
    @Schema(description = "Represents a business strategy goal resource type.")
    BUSINESS_STRATEGY_GOAL,

    @JsonProperty("ISSUE")
    @Schema(description = "Represents an issue resource type.")
    ISSUE,

    @JsonProperty("ACTION")
    @Schema(description = "Represents an action resource type.")
    ACTION,

    @JsonProperty("GOAL")
    @Schema(description = "Represents a goal resource type.")
    GOAL,

    @JsonProperty("BUSINESS_STRATEGY_CONFIGURATION")
    @Schema(description = "Represents a business strategy configuration resource type.")
    BUSINESS_STRATEGY_CONFIGURATION,

    @JsonProperty("ISSUE_PRIORITY")
    @Schema(description = "Represents an issue priority resource type.")
    ISSUE_PRIORITY,

    @JsonProperty("ISSUE_PRIORITY_SET")
    @Schema(description = "Represents an issue priority set resource type.")
    ISSUE_PRIORITY_SET,

    @JsonProperty("ISSUE_SEVERITY")
    @Schema(description = "Represents an issue severity resource type.")
    ISSUE_SEVERITY,

    @JsonProperty("ISSUE_SEVERITY_SET")
    @Schema(description = "Represents an issue severity set resource type.")
    ISSUE_SEVERITY_SET,

    @JsonProperty("ISSUE_CATEGORY")
    @Schema(description = "Represents an issue category resource type.")
    ISSUE_CATEGORY,

    @JsonProperty("ISSUE_CATEGORY_SET")
    @Schema(description = "Represents an issue category set resource type.")
    ISSUE_CATEGORY_SET,

    @JsonProperty("PROJECT")
    @Schema(description = "Represents a project resource type.")
    PROJECT,

    @JsonProperty("PROJECT_TASK")
    @Schema(description = "Represents a project task resource type.")
    PROJECT_TASK,

    @JsonProperty("TASK")
    @Schema(description = "Represents a task resource type.")
    TASK,

    @JsonProperty("PROJECT_CONFIGURATION")
    @Schema(description = "Represents a project configuration resource type.")
    PROJECT_CONFIGURATION,

    @JsonProperty("TASK_PRIORITY")
    @Schema(description = "Represents a task priority resource type.")
    TASK_PRIORITY,

    @JsonProperty("TASK_STATUS")
    @Schema(description = "Represents a task status resource type.")
    TASK_STATUS,

    @JsonProperty("TASK_CATEGORY")
    @Schema(description = "Represents a task category resource type.")
    TASK_CATEGORY,

    @JsonProperty("TASK_PRIORITY_SET")
    @Schema(description = "Represents a task priority set resource type.")
    TASK_PRIORITY_SET,

    @JsonProperty("TASK_STATUS_SET")
    @Schema(description = "Represents a task status set resource type.")
    TASK_STATUS_SET,

    @JsonProperty("TASK_CATEGORY_SET")
    @Schema(description = "Represents a task category set resource type.")
    TASK_CATEGORY_SET,
}

val <T> T.resourceName: String
    get() = if (this is BaseAuditedResource<*>) {
        this.resourceType.toString()
    } else {
        "Unknown Resource"
    }
