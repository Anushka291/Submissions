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
## 1. CODING SOCIAL NETWORKS
## =========================================================================

## --- Build `common_committees` -------------------------------------------
## The raw committee_memberships table is (committee/subcommittee, member).
## To build a person-to-person network we need pairs of members who sit on
## at least one committee/subcommittee together. We self-join the
## membership table on committee id (thomas_id), keep each unordered pair
## once (bioguide_id.x < bioguide_id.y so A-B and B-A aren't both kept, and
## a member is never paired with themselves), and count how many
## committees/subcommittees each pair shares (this becomes the edge weight).

cm <- committee_memberships %>%
  select(thomas_id, bioguide_id) %>%
  distinct()

common_committees <- cm %>%
  inner_join(cm, by = "thomas_id", relationship = "many-to-many") %>%
  filter(bioguide_id.x < bioguide_id.y) %>%
  distinct(thomas_id, bioguide_id.x, bioguide_id.y) %>%
  group_by(bioguide_id.x, bioguide_id.y) %>%
  summarise(n_committees = n(), .groups = "drop") %>%
  rename(from = bioguide_id.x, to = bioguide_id.y)

head(common_committees)

## --- 1a. Graph from common_committees, vertex/edge counts -----------------
g <- graph_from_data_frame(common_committees, directed = FALSE)
cat("1a) Vertices:", vcount(g), " | Edges:", ecount(g), "\n")
# 1a) Vertices: 532 | Edges: 22520

## --- 1b. Graph restricted to pairs sharing >= 3 committees ----------------
common_committees_3plus <- common_committees %>% filter(n_committees >= 3)
g3 <- graph_from_data_frame(common_committees_3plus, directed = FALSE)
cat("1b) Edges with >=3 shared committees:", ecount(g3), "\n")
# 1b) Edges with >=3 shared committees: 5863

## --- 1c. Simple ggraph visualization of the >=3 network -------------------
meta <- congress %>% select(bioguide_id, full_name, type, party)
V(g3)$party <- meta$party[match(V(g3)$name, meta$bioguide_id)]
V(g3)$type  <- meta$type[match(V(g3)$name, meta$bioguide_id)]

set.seed(42)
p_g3 <- ggraph(g3, layout = "fr") +
  geom_edge_link(alpha = 0.2, colour = "grey60") +
  geom_node_point(aes(color = party), size = 2.2) +
  scale_color_manual(values = c("Democrat" = "#2E5EAA",
                                 "Republican" = "#C0392B",
                                 "Independent" = "#7F8C8D")) +
  theme_void() +
  labs(title = "Congress members sharing >= 3 committees together",
       color = "Party")
print(p_g3)
ggsave("network_3plus.png", p_g3, width = 8, height = 7, dpi = 150)

## --- 1d. Senators-only network ---------------------------------------------
senator_ids <- congress %>% filter(type == "sen") %>% pull(bioguide_id)

common_committees_sen <- common_committees %>%
  filter(from %in% senator_ids & to %in% senator_ids)

g_sen <- graph_from_data_frame(common_committees_sen, directed = FALSE,
                                vertices = data.frame(bioguide_id = senator_ids))
cat("1d) Senators-only network -> Vertices:", vcount(g_sen),
    " | Edges:", ecount(g_sen), "\n")
# 1d) Senators-only network -> Vertices: 100 | Edges: 3187

sen_meta <- congress %>% filter(type == "sen") %>% select(bioguide_id, party)
V(g_sen)$party <- sen_meta$party[match(V(g_sen)$name, sen_meta$bioguide_id)]

set.seed(42)
p_sen <- ggraph(g_sen, layout = "fr") +
  geom_edge_link(alpha = 0.15, colour = "grey60") +
  geom_node_point(aes(color = party), size = 2.6) +
  scale_color_manual(values = c("Democrat" = "#2E5EAA",
                                 "Republican" = "#C0392B",
                                 "Independent" = "#7F8C8D")) +
  theme_void() +
  labs(title = "Senate-only committee co-membership network", color = "Party")
print(p_sen)
ggsave("network_senators.png", p_sen, width = 8, height = 7, dpi = 150)