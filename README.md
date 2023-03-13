# CSV_DB_Handling demo

## Objectives
Sort and import order data.

Given a .csv file with order records, the application must generate another file with the records sorted by order number. In addition, it must import all that data into a database and display, upon completion of processing, a summary of the number of orders of each type according to different columns.

To perform the exercise, we attach links to download 2 files, with the same data structure (using comma <,> as separator).
- shortCSV: https://drive.google.com/u/0/uc?id=1pkmcx7M1KzVRwQxRBkWBkg6GANFN2RRw&export=download
- longCSV: https://drive.google.com/u/0/uc?id=1lLMqoS4dxaRM3NPFUsacq0Ca8_6RrygA&export=download&confirm=t&uuid=ecd60b84-727b-4d85-bc9b-a95d69fc7b59&at=ALgDtsxD9bPy7fss9vZTm2ocXLJ-:1678668455845

Region
Country
Item Type
Sales Channel
Order Priority
Order Date
Order ID
Ship Date
Units Sold
Unit Price
Unit Cost
Total Revenue
Total Cost
Total Profit

- The application will receive as input parameter the path of the file to be processed.
- The field by which the resulting file must be sorted is orderId.
- The final summary should include the count for each type of the fields: Region, Country, Item Type, Sales Channel, Order Priority.

## Usage
- Install Java 17
- Install MySQL
- Create user in MySQL
- adjust env variables in run.sh (DB connection) accordingly
- Run ./install.sh
- Run ./run.sh (for short CSV)
- Run ./run.sh -l (for long CSV, expected execution time: 1 minute or less)
