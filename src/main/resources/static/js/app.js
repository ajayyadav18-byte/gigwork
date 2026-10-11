
document.addEventListener("DOMContentLoaded", () => {
    // Mobile navigation
    const menuToggle = document.getElementById("menuToggle");
    const navLinks = document.getElementById("navLinks");

    if (menuToggle && navLinks) {
        menuToggle.addEventListener("click", () => {
            const isOpen = navLinks.classList.toggle("is-open");
            menuToggle.setAttribute("aria-expanded", String(isOpen));
        });

        navLinks.querySelectorAll("a").forEach((link) => {
            link.addEventListener("click", () => {
                navLinks.classList.remove("is-open");
                menuToggle.setAttribute("aria-expanded", "false");
            });
        });
    }

    // Gig search and category filters
    const searchInput = document.getElementById("gigSearch");
    const categoryFilter = document.getElementById("categoryFilter");
    const searchButton = document.getElementById("searchButton");
    const categoryPills = document.querySelectorAll(".category-pill");
    const gigCards = document.querySelectorAll(".gig-card");
    const emptyState = document.getElementById("emptyState");

    let selectedCategory = "all";

    function filterGigs() {
        const query = searchInput
            ? searchInput.value.trim().toLowerCase()
            : "";

        let visibleCount = 0;

        gigCards.forEach((card) => {
            const cardCategory = card.dataset.category || "";
            const searchableText = (
                (card.dataset.search || "") + " " +
                card.textContent
            ).toLowerCase();

            const matchesCategory =
                selectedCategory === "all" ||
                cardCategory === selectedCategory;

            const matchesQuery =
                query.length === 0 ||
                searchableText.includes(query);

            const shouldShow = matchesCategory && matchesQuery;

            card.hidden = !shouldShow;

            if (shouldShow) {
                visibleCount++;
            }
        });

        if (emptyState) {
            emptyState.hidden = visibleCount > 0;
        }
    }

    function selectCategory(category) {
        selectedCategory = category;

        if (categoryFilter) {
            categoryFilter.value = category;
        }

        categoryPills.forEach((pill) => {
            const isSelected = pill.dataset.category === category;

            pill.classList.toggle("selected", isSelected);
            pill.setAttribute("aria-pressed", String(isSelected));
        });

        filterGigs();
    }

    categoryPills.forEach((pill) => {
        pill.setAttribute(
            "aria-pressed",
            String(pill.dataset.category === selectedCategory)
        );

        pill.addEventListener("click", () => {
            selectCategory(pill.dataset.category || "all");
        });
    });

    if (categoryFilter) {
        categoryFilter.addEventListener("change", () => {
            selectCategory(categoryFilter.value);
        });
    }

    if (searchInput) {
        searchInput.addEventListener("input", filterGigs);

        searchInput.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                filterGigs();
                document.getElementById("gigs")?.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
            }

            if (event.key === "Escape") {
                searchInput.value = "";
                filterGigs();
            }
        });
    }

    if (searchButton) {
        searchButton.addEventListener("click", () => {
            filterGigs();

            document.getElementById("gigs")?.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

            searchInput?.focus({ preventScroll: true });
        });
    }

    // Automatically update the footer year.
    const currentYear = document.getElementById("currentYear");

    if (currentYear) {
        currentYear.textContent = String(new Date().getFullYear());
    }

    // Apply the initial gig filter.
    filterGigs();
});

