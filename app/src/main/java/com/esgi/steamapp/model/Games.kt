package com.esgi.steamapp.model

data class Games(
    val `431960`: X431960
) {
    data class X431960(
        val `data`: Data,
        val success: Boolean
    ) {
        data class Data(
            val about_the_game: String,
            val achievements: Achievements,
            val background: String,
            val background_raw: String,
            val categories: List<Category>,
            val content_descriptors: ContentDescriptors,
            val detailed_description: String,
            val developers: List<String>,
            val dlc: List<Int>,
            val genres: List<Genre>,
            val header_image: String,
            val is_free: Boolean,
            val legal_notice: String,
            val linux_requirements: List<Any>,
            val mac_requirements: List<Any>,
            val movies: List<Movy>,
            val name: String,
            val package_groups: List<PackageGroup>,
            val packages: List<Int>,
            val pc_requirements: PcRequirements,
            val platforms: Platforms,
            val price_overview: PriceOverview,
            val publishers: List<String>,
            val recommendations: Recommendations,
            val release_date: ReleaseDate,
            val required_age: Int,
            val screenshots: List<Screenshot>,
            val short_description: String,
            val steam_appid: Int,
            val support_info: SupportInfo,
            val supported_languages: String,
            val type: String,
            val website: String
        ) {
            data class Achievements(
                val highlighted: List<Highlighted>,
                val total: Int
            ) {
                data class Highlighted(
                    val name: String,
                    val path: String
                )
            }

            data class Category(
                val description: String,
                val id: Int
            )

            data class ContentDescriptors(
                val ids: List<Any>,
                val notes: Any
            )

            data class Genre(
                val description: String,
                val id: String
            )

            data class Movy(
                val highlight: Boolean,
                val id: Int,
                val mp4: Mp4,
                val name: String,
                val thumbnail: String,
                val webm: Webm
            ) {
                data class Mp4(
                    val `480`: String,
                    val max: String
                )

                data class Webm(
                    val `480`: String,
                    val max: String
                )
            }

            data class PackageGroup(
                val description: String,
                val display_type: Int,
                val is_recurring_subscription: String,
                val name: String,
                val save_text: String,
                val selection_text: String,
                val subs: List<Sub>,
                val title: String
            ) {
                data class Sub(
                    val can_get_free_license: String,
                    val is_free_license: Boolean,
                    val option_description: String,
                    val option_text: String,
                    val packageid: Int,
                    val percent_savings: Int,
                    val percent_savings_text: String,
                    val price_in_cents_with_discount: Int
                )
            }

            data class PcRequirements(
                val minimum: String,
                val recommended: String
            )

            data class Platforms(
                val linux: Boolean,
                val mac: Boolean,
                val windows: Boolean
            )

            data class PriceOverview(
                val currency: String,
                val discount_percent: Int,
                val `final`: Int,
                val final_formatted: String,
                val initial: Int,
                val initial_formatted: String
            )

            data class Recommendations(
                val total: Int
            )

            data class ReleaseDate(
                val coming_soon: Boolean,
                val date: String
            )

            data class Screenshot(
                val id: Int,
                val path_full: String,
                val path_thumbnail: String
            )

            data class SupportInfo(
                val email: String,
                val url: String
            )
        }
    }
}