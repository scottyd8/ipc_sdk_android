## [1.0.33] - 2017-05-17
### Added
-	Android Demo App
-	Manual card entry on PED
### Fixed
-	Unsettled debit transactions now correctly fall back to void
-	When this void occurs, the entire transaction will be voided, including cashback amounts
-	This must be taken into account by the merchant when processing a void on debit
-	Assorted bug fixes




## [1.0.32] - 2017-05-05
### Added
-	Android SDK Documentation
-	iOS SDK Documentation Update
### Fixed
-	Android SDK Miura Naming bug

## [1.0.32] - 2017-04-24
### Added 
-	Tip functionality on Pin-pad
-	Debit cashback support 
-	Auto-select AID functionality. A card with both Credit and Debit AIDs will prompt for Credit/Debit. If multiple AIDs are still present for the chosen type, the AID will be chosen automatically.
-	Update Transaction functionality to append level 2 data to an existing transaction
-	Retrieve a specific transaction by transactionID
-	Search and return an array of completed transactions

### Fixed 
-	Numerous bug fixes
