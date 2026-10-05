package com.nichetrendradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nichetrendradar.ui.theme.Background
import com.nichetrendradar.ui.theme.PrimaryBright
import com.nichetrendradar.ui.theme.TextPrimary
import com.nichetrendradar.ui.theme.TextSecondary
import com.nichetrendradar.ui.theme.Surface

enum class LegalDocument(val title: String) {
    PRIVACY("Privacy Policy"),
    TERMS("Terms of Service"),
    DATA_USAGE("Data Usage Policy")
}

private data class LegalSection(val heading: String, val body: String)

private fun sections(document: LegalDocument): List<LegalSection> = when (document) {
    LegalDocument.PRIVACY -> listOf(
        LegalSection("Effective Date", "5 October 2026 • Version 1.0"),
        LegalSection("1. Overview", "Niche Trend Radar helps users discover trends, configure a niche radar, generate AI-assisted content ideas and save ideas for later. This Privacy Policy explains what information the app handles, why it is used and the controls available to you."),
        LegalSection("2. Information We Handle", "Account information may include your email address, user ID, authentication/session information and account timestamps. Radar information may include your selected niche, keywords and platforms. Saved content may include ideas, titles, hooks, platforms and related trend context. Radar activity may include the niche, platform and date/time of requests."),
        LegalSection("3. How We Use Information", "We use information to authenticate your account, operate the radar, provide trend results, generate AI-assisted ideas, save and display your Library, maintain security, provide support, export your data and process deletion requests."),
        LegalSection("4. AI Processing", "AI features may process the niche, keywords, trend context and other inputs you submit to generate content ideas. Do not submit passwords, financial information, government identification numbers, medical records or other sensitive personal information into radar or AI fields."),
        LegalSection("5. Network and Security", "Authenticated API communication is intended to use HTTPS. Authentication credentials or session tokens are stored locally on the device for the active session. No security measure can guarantee absolute security."),
        LegalSection("6. Data Sharing", "Information may be processed by service providers needed to operate the app, such as hosting, authentication, AI and trend/data services. We do not sell personal information."),
        LegalSection("7. Retention and Deletion", "Information is retained while needed to provide the service or meet legitimate operational requirements. You can use the app's data controls to export or delete available account data. Account deletion is intended to remove the account and associated application data."),
        LegalSection("8. Your Choices", "Depending on applicable law, you may have rights to access, export, correct or delete your information and to manage certain preferences. The app provides account and data-control features for these purposes."),
        LegalSection("9. Children", "Niche Trend Radar is not intended for children who are not permitted to use the service under applicable law."),
        LegalSection("10. Changes", "We may update this policy when the service, processing practices or legal requirements change. The effective date and version will be updated when material changes are published."),
        LegalSection("11. Contact", "Support contact: [YOUR SUPPORT EMAIL]. Replace this placeholder with the official support address before public launch.")
    )
    LegalDocument.TERMS -> listOf(
        LegalSection("Effective Date", "5 October 2026 • Version 1.0"),
        LegalSection("1. Acceptance", "By using Niche Trend Radar, you agree to these Terms of Service. If you do not agree, do not use the service."),
        LegalSection("2. Service", "The service may provide niche and keyword trend discovery, trend information, AI-assisted content ideas, saved ideas, radar configuration and account data export. Features may change over time."),
        LegalSection("3. Account Responsibilities", "You are responsible for maintaining access to your account, protecting your credentials and using the service only for lawful purposes. Do not share credentials or attempt to bypass security controls."),
        LegalSection("4. Acceptable Use", "You must not misuse the service, interfere with its operation, attempt unauthorized access, submit unlawful content or use generated information in a way that violates applicable law or third-party rights."),
        LegalSection("5. AI-Generated Content", "AI-generated ideas are assistive outputs, not guarantees of originality, accuracy, performance or suitability. Review generated content before publishing or relying on it."),
        LegalSection("6. Trend Information", "Trend scores, growth labels and other trend information depend on connected data sources. Results may be incomplete, delayed or inaccurate and should be independently evaluated."),
        LegalSection("7. User Content", "You retain responsibility for content and inputs you submit. You represent that you have the rights necessary to submit them and that they do not violate applicable law or third-party rights."),
        LegalSection("8. Intellectual Property", "The app, its software, branding and original service materials are protected by applicable intellectual-property laws. These Terms do not transfer ownership of the service to you."),
        LegalSection("9. Availability", "We may experience maintenance, outages, service-provider failures or feature changes. We do not guarantee uninterrupted or error-free availability."),
        LegalSection("10. Disclaimer and Liability", "The service is provided subject to applicable law without guarantees that every result will be accurate or fit a particular purpose. To the extent permitted by law, liability is limited to the scope allowed by applicable law."),
        LegalSection("11. Suspension or Termination", "Access may be suspended or terminated for security, misuse, legal or operational reasons. You may stop using the service and request account deletion through available controls."),
        LegalSection("12. Changes", "We may update these Terms as the service evolves. Continued use after an updated version becomes effective constitutes acceptance where permitted by law."),
        LegalSection("13. Contact", "Support contact: [YOUR SUPPORT EMAIL]. Replace this placeholder with the official support address before public launch.")
    )
    LegalDocument.DATA_USAGE -> listOf(
        LegalSection("Effective Date", "5 October 2026 • Version 1.0"),
        LegalSection("1. Purpose", "This policy explains how information is used to operate Niche Trend Radar, personalize the experience, support radar requests and provide AI-assisted content features."),
        LegalSection("2. Data Categories", "The service may handle account information, radar configuration such as niche and keywords, selected platforms, saved ideas, preferences and radar activity needed to provide the product."),
        LegalSection("3. Trend and Search Data", "Niche, keyword and platform inputs provide context for radar requests and may be recorded as radar activity so the service can support account features and data controls."),
        LegalSection("4. AI Processing", "AI features use relevant trend and niche context you provide to generate content ideas. Avoid entering passwords, financial information, government IDs, medical records or other sensitive information into AI or radar inputs."),
        LegalSection("5. Personalization", "Preferences such as personalized recommendations may be stored locally and used to adjust the app experience."),
        LegalSection("6. Analytics", "Where enabled, usage analytics may be used to understand reliability, performance and product usage. You can manage the available analytics preference in Account & Privacy."),
        LegalSection("7. Marketing Communications", "Marketing communications are optional where the product provides a marketing preference. Product-critical service communications may still be sent when necessary."),
        LegalSection("8. Security", "Data is intended to be protected through authenticated API access and HTTPS in transit. Security practices may evolve as the service develops."),
        LegalSection("9. Export", "The app provides a Download My Data control that requests available account data and generates a PDF export for sharing or storage."),
        LegalSection("10. Deletion", "Available data-control features can clear saved ideas, clear radar history and request account deletion. Account deletion is intended to remove the account and associated application data."),
        LegalSection("11. Sharing and Providers", "Data may be processed by service providers that support hosting, authentication, AI, trend/data services and related app operations. We do not sell personal information."),
        LegalSection("12. Changes", "This policy may be updated when data practices or service functionality changes. The effective date and version will identify the current document."),
        LegalSection("13. Contact", "Support contact: [YOUR SUPPORT EMAIL]. Replace this placeholder with the official support address before public launch.")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentScreen(navController: NavController, document: LegalDocument) {
    val content = sections(document)
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(document.title, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryBright)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Background),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = padding.calculateTopPadding() + 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Text(
                        "Niche Trend Radar",
                        color = PrimaryBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
            items(content) { section ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    Text(section.heading, color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(section.body, color = TextSecondary, modifier = Modifier.padding(top = 6.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
            item {
                Text(
                    "This in-app document is a product draft and should be reviewed for your jurisdiction and finalized with the official support contact before public launch.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
