# Downloading a Specific Folder Using Git Sparse Checkout

## Objective
To download only a specific directory from a GitHub repository without checking out all repository files, using Git Sparse Checkout.

## Repository Details
- **GitHub Username:** Anushka291
- **Repository Name:** Submissions
- **Folder Path:** `Final Submissions/DCS/Exp 1`

## Prerequisites
1. Git must be installed on the system.
2. An active internet connection is required.
3. Access to the GitHub repository.

## Procedure

### Step 1: Open PowerShell
Open PowerShell and navigate to the directory where you want to save the repository.

```powershell
cd "D:\AISSMS IOIT\7th semester\Final Submissions"
```

### Step 2: Clone the Repository

```powershell
git clone --filter=blob:none --no-checkout https://github.com/Anushka291/Submissions.git
```

### Step 3: Navigate to the Repository

```powershell
cd Submissions
```

### Step 4: Initialize Sparse Checkout

```powershell
git sparse-checkout init --cone
```

### Step 5: Select the Required Directory

```powershell
git sparse-checkout set "Final Submissions/DCS/Exp 1"
```

### Step 6: Checkout the Main Branch

```powershell
git checkout main
```

**Note:** If the default branch is named `master`, use `git checkout master` instead.

## Expected Directory Structure

After successful execution, the selected directory will be available in the working tree.

```text
Submissions/
└── Final Submissions/
    └── DCS/
        └── Exp 1/
            ├── README.md
            └── Other Experiment Files
```

## Advantages of Git Sparse Checkout
- Downloads only the required working-tree files.
- Avoids checking out unnecessary directories.
- Saves local disk space.
- Allows working with a specific directory of a large repository.

## Conclusion
Git Sparse Checkout allows users to retrieve a specific directory from a GitHub repository without checking out the entire repository's contents.
