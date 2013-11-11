package metridoc.core

import org.apache.commons.lang.StringUtils

class ManageConfigService {

    public static final String REPORT_ISSUES = "ReportIssues"

    def updateReportUserEmails(String reportIssueEmails, Map flash) {
        if(!reportIssueEmails) return
        def validEmails = []
        log.debug "loading emails [$reportIssueEmails]"
        boolean allAreValid = true

        reportIssueEmails.split(/\s/).each {
            log.debug "loading email [$it]"
            def email = NotificationEmails.findByEmail(it)
            if(email == null) {
                email = new NotificationEmails (
                        email: it,
                        scope: REPORT_ISSUES
                )
                if(!email.save()) {
                    flash.alerts << "[$it] is not a valid email"
                    allAreValid = false
                }
                else {
                    validEmails << email.email
                }
            }
            else {
                validEmails << email.email
            }
        }

        if (allAreValid) {
            removeNoLongerUsedEmails(validEmails as Set)
        }
    }

    protected void removeNoLongerUsedEmails(Set<String> validEmails) {

        NotificationEmails.findByScope(ManageConfigService.REPORT_ISSUES).each {
            if(!validEmails.contains(it.email)) {
                it.delete()
            }
        }
    }

    String getReportIssueEmails() {
        StringBuilder emails = new StringBuilder()

        NotificationEmails.findAllByScope(REPORT_ISSUES).each {
            emails.append(it.email)
            emails.append(",")
        }

        String response
        if (emails) {
            response = StringUtils.chop(emails.toString())
        }

        log.debug "using emails [$response] for reporting errors issues"
        return response
    }
}
