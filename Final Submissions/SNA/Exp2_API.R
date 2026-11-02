# ============================================================
# EXPERIMENT 2 - WORKING WITH APIs
# Offline version using supplied RData files
# ============================================================

library(dplyr)
library(stringr)

# ------------------------------------------------------------
# Load data
# ------------------------------------------------------------

data_dir <- "."

load(file.path(data_dir, "congress.RData"))
load(file.path(data_dir, "senator_tweets.RData"))

# ------------------------------------------------------------
# Prepare senator data
# ------------------------------------------------------------

senators <- congress %>%
  filter(type == "sen") %>%
  left_join(congress_contact, by = "bioguide_id")

cat("\n========================================\n")
cat("EXPERIMENT 2 - WORKING WITH APIs\n")
cat("Using supplied senator tweet dataset\n")
cat("========================================\n\n")


# ============================================================
# 2a. How many senator Twitter accounts were found?
# ============================================================

handles <- senators$twitter

handles <- handles[
  !is.na(handles) &
  handles != ""
]

handles <- unique(handles)

# Count accounts represented in the supplied tweet sample
sample_handles <- unique(senator_tweet_sample$screen_name)

sample_handles <- sample_handles[
  !is.na(sample_handles) &
  sample_handles != ""
]

n_found <- length(intersect(
  tolower(handles),
  tolower(sample_handles)
))

n_queried <- length(handles)

cat("2a) Senator Twitter accounts represented in supplied data:\n")
cat("    Found:", n_found, "\n")
cat("    Listed senator accounts:", n_queried, "\n\n")


# ============================================================
# 2b. Average number of published tweets by gender
# ============================================================

# The supplied senator_tweets.RData contains tweet records,
# rather than live Twitter account status counts.
#
# Therefore we calculate the average number of tweets in the
# supplied sample for each gender.

tweet_sample_gender <- senator_tweet_sample %>%
  mutate(screen_lower = tolower(screen_name)) %>%
  left_join(
    senators %>%
      select(twitter, gender) %>%
      mutate(twitter_lower = tolower(twitter)),
    by = c("screen_lower" = "twitter_lower")
  )

avg_tweets_by_gender <- tweet_sample_gender %>%
  filter(!is.na(gender)) %>%
  group_by(gender) %>%
  summarise(
    avg_tweets_in_sample = n() / n_distinct(screen_name),
    n_tweets = n(),
    n_senators = n_distinct(screen_name),
    .groups = "drop"
  )

cat("2b) Average tweets represented in supplied sample by gender:\n")
print(avg_tweets_by_gender)
cat("\n")


# ============================================================
# 2c. Correlation between senator age and Twitter account
#     creation date
# ============================================================

# The supplied RData contains senator birth dates, but the
# offline dataset does not provide live account_created_at
# information.
#
# Therefore we calculate senator age and report that the live
# account-creation variable is unavailable.

age_data <- senators %>%
  filter(!is.na(birthdate)) %>%
  mutate(
    age_years =
      as.numeric(
        difftime(Sys.Date(), birthdate, units = "days")
      ) / 365.25
  )

cat("2c) Senator age information:\n")
cat("    Senators with available birthdate:",
    nrow(age_data), "\n")

cat("    Live Twitter account creation date is not available\n")
cat("    in the supplied RData dataset.\n")
cat("    Therefore the requested age/account-creation\n")
cat("    correlation cannot be calculated offline.\n\n")


# ============================================================
# 2d. Last two tweets per senator -> average retweets by gender
# ============================================================

# Use the supplied senator_tweet_sample.
#
# For each senator, select up to the last two records available
# in the supplied dataset.

last_two <- senator_tweet_sample %>%
  filter(
    !is.na(screen_name),
    screen_name != ""
  ) %>%
  group_by(screen_name) %>%
  slice_head(n = 2) %>%
  ungroup()

last_two <- last_two %>%
  mutate(screen_lower = tolower(screen_name)) %>%
  left_join(
    senators %>%
      select(twitter, gender) %>%
      mutate(twitter_lower = tolower(twitter)),
    by = c("screen_lower" = "twitter_lower")
  )

avg_rt_by_gender <- last_two %>%
  filter(!is.na(gender)) %>%
  group_by(gender) %>%
  summarise(
    avg_retweets = mean(retweet_count, na.rm = TRUE),
    n_tweets = n(),
    n_senators = n_distinct(screen_name),
    .groups = "drop"
  )

cat("2d) Average retweets for up to last two supplied tweets,\n")
cat("    by senator gender:\n")

print(avg_rt_by_gender)

cat("\n========================================\n")
cat("EXPERIMENT 2 COMPLETED\n")
cat("========================================\n")