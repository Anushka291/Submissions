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
## 3. MODELING A NETWORK
## =========================================================================

ref_date <- Sys.Date()   # or set a fixed date, e.g. as.Date("2026-09-10")

members <- congress %>%
  left_join(congress_contact, by = "bioguide_id") %>%
  mutate(
    age = as.numeric(difftime(ref_date, birthdate, units = "days")) / 365.25,
    n_social = (twitter  != "" & !is.na(twitter))  +
               (facebook != "" & !is.na(facebook)) +
               (youtube  != "" & !is.na(youtube))
  )

## --- 3a. Boxplot: approximate age by gender --------------------------------
p_age_gender <- ggplot(members, aes(x = gender, y = age, fill = gender)) +
  geom_boxplot() +
  scale_fill_manual(values = c("F" = "#D6604D", "M" = "#4393C3")) +
  labs(title = "Approximate age of Congress members by gender",
       x = "Gender", y = "Age (years)") +
  theme_minimal()
print(p_age_gender)
ggsave("age_by_gender_box.png", p_age_gender, width = 6, height = 5, dpi = 150)

members %>% group_by(gender) %>%
  summarise(mean_age = mean(age), median_age = median(age), n = n())

## --- 3b. Scatter: age vs. number of social media accounts + trendline -----
p_age_social <- ggplot(members, aes(x = age, y = n_social)) +
  geom_jitter(width = 0, height = 0.08, alpha = 0.5, color = "#2C3E50") +
  geom_smooth(method = "lm", se = TRUE, color = "#C0392B") +
  labs(title = "Age vs. Number of social media accounts",
       x = "Age (years)",
       y = "Number of social media accounts (Twitter/Facebook/YouTube)") +
  theme_minimal()
print(p_age_social)
ggsave("age_vs_social.png", p_age_social, width = 6, height = 5, dpi = 150)

cor.test(members$age, members$n_social)

## --- 3c. Linear regression: n_social ~ type + age + party + gender --------
members <- members %>%
  mutate(
    type   = relevel(factor(type),   ref = "rep"),
    party  = relevel(factor(party),  ref = "Democrat"),
    gender = relevel(factor(gender), ref = "M")
  )

fit <- lm(n_social ~ type + age + party + gender, data = members)
summary(fit)
tidy(fit, conf.int = TRUE)