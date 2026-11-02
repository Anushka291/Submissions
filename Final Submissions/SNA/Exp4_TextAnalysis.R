library(dplyr)
library(tidyr)
library(stringr)
library(igraph)
library(ggraph)
library(ggplot2)
library(broom)

data_dir <- "."

load(file.path(data_dir, "congress.RData"))
load(file.path(data_dir, "committees.RData"))
load(file.path(data_dir, "senator_tweets.RData"))
load(file.path(data_dir, "senator_wiki.RData"))

## =========================================================================
## 4. INTRODUCTION TO TEXT ANALYSIS
## =========================================================================

tweets <- senator_tweet_sample %>%
  mutate(n_mentions = str_count(text, "@\\S+"))

## --- 4a. Correlation between # mentions and # favorites --------------------
cor.test(tweets$n_mentions, tweets$favorite_count)                      # Pearson
cor.test(tweets$n_mentions, tweets$favorite_count, method = "spearman") # rank-based

## --- 4b. Average number of mentions per tweet, by senator gender ----------
lookup <- congress_contact %>%
  select(bioguide_id, twitter) %>%
  left_join(congress %>% select(bioguide_id, gender), by = "bioguide_id") %>%
  mutate(twitter_lower = tolower(twitter))

tweets_gender <- tweets %>%
  mutate(screen_lower = tolower(screen_name)) %>%
  left_join(lookup, by = c("screen_lower" = "twitter_lower"))

# Note: a handful of screen_names belong to senators who have since left
# office and are therefore missing from the current congress_contact table;
# those rows have gender = NA and are dropped from the by-gender summary.
tweets_gender %>%
  filter(!is.na(gender)) %>%
  group_by(gender) %>%
  summarise(avg_mentions = mean(n_mentions),
            n_tweets = n(),
            n_senators = n_distinct(screen_name))
