# Practice bank review, wave 2: skills of the Data, Cloud and DevOps roles not in wave 1

Check every answer. Correct options are **bold**. Fix a question in the `bank_*.py` file named in its heading, run `python gen_bank.py`
again, then move `V9__starter_bank_wave2.sql` to `db/migration/`.

## Excel (bank_data.py)

- [ ] **MC, easy:** Which of these are Excel functions?
  - A. **SUM**
  - B. **AVERAGE**
  - C. **VLOOKUP**
  - D. SELECT
- [ ] **MC, easy:** Which statements about cell references are true?
  - A. **A1 is a relative reference**
  - B. **$A$1 is an absolute reference**
  - C. **A relative reference changes when the formula is copied**
  - D. An absolute reference changes when the formula is copied
- [ ] **MC, easy:** Which help sort and filter data in Excel?
  - A. **The Sort button**
  - B. **AutoFilter**
  - C. **Tables with drop-downs in the header**
  - D. The Print button
- [ ] **MC, medium:** Which statements about VLOOKUP are true?
  - A. **It searches the first column of a range**
  - B. **FALSE as the last argument asks for an exact match**
  - C. **It cannot return a value from a column to the left of the search column**
  - D. It returns every match at once
- [ ] **MC, medium:** Which statements about XLOOKUP are true?
  - A. **It can look left or right**
  - B. **It matches exactly by default**
  - C. **It can return a chosen value when nothing is found**
  - D. It works only on sorted data
- [ ] **MC, medium:** Which statements about PivotTables are true?
  - A. **They summarise large data quickly**
  - B. **You drag fields into rows, columns and values**
  - C. **They can group dates by month**
  - D. They change the source data
- [ ] **MC, medium:** Which functions count or sum with a condition?
  - A. **COUNTIF**
  - B. **SUMIF**
  - C. **SUMIFS**
  - D. CONCAT
- [ ] **MC, hard:** Which statements about INDEX and MATCH are true?
  - A. **MATCH finds the position of a value**
  - B. **INDEX returns the value at a position**
  - C. **Together they can look up in any direction**
  - D. They work only in a single row
- [ ] **MC, hard:** Which statements about Power Query in Excel are true?
  - A. **It imports and cleans data from many sources**
  - B. **Its steps are recorded and can be refreshed**
  - C. **It can merge and append tables**
  - D. It only draws charts
- [ ] **MC, hard:** Which statements about dynamic arrays in Excel are true?
  - A. **One formula can spill results into several cells**
  - B. **FILTER returns the rows that meet a condition**
  - C. **UNIQUE returns distinct values**
  - D. A spill range overwrites cells that already hold data
- [ ] **Blank, easy:** Every Excel formula starts with the ________ sign.  
  Accepted: =, equals, equal
- [ ] **Blank, easy:** The function ________ adds up a range of numbers.  
  Accepted: SUM
- [ ] **Blank, easy:** A reference such as $A$1 that does not change when copied is an ________ reference.  
  Accepted: absolute
- [ ] **Blank, medium:** A ________ summarises data by dragging fields into rows, columns and values.  
  Accepted: PivotTable, pivot table
- [ ] **Blank, medium:** The function ________ counts the cells that meet one condition.  
  Accepted: COUNTIF
- [ ] **Blank, medium:** ________ formatting changes a cell's colour based on its value.  
  Accepted: Conditional
- [ ] **Blank, medium:** The function ________ returns a value from a table by searching its first column.  
  Accepted: VLOOKUP
- [ ] **Blank, hard:** The function ________ replaces an error result with a value you choose.  
  Accepted: IFERROR
- [ ] **Blank, hard:** Excel's tool for importing and transforming data with recorded steps is Power ________.  
  Accepted: Query
- [ ] **Blank, hard:** When a dynamic array formula fills the neighbouring cells, it is said to ________.  
  Accepted: spill
- [ ] **Brief, easy:** What is the difference between relative and absolute cell references?
  - Key fact: a relative reference such as A1 changes when the formula is copied
  - Key fact: an absolute reference such as $A$1 stays fixed
  - Key fact: mixed references such as $A1 fix only the column or only the row
  - Keywords (grading without AI): relative, absolute, $, copy, mixed
- [ ] **Brief, medium:** When would you use a PivotTable?
  - Key fact: to summarise a large table by groups, such as sales per region and month
  - Key fact: to drag fields into rows, columns, values and filters and explore data quickly
  - Key fact: to refresh the summary when the source data changes
  - Keywords (grading without AI): PivotTable, summarise, group, rows, refresh
- [ ] **Brief, medium:** Compare VLOOKUP, XLOOKUP and INDEX with MATCH.
  - Key fact: VLOOKUP searches the first column and returns a value from a column to its right
  - Key fact: XLOOKUP searches in any direction, matches exactly by default and handles missing values
  - Key fact: INDEX with MATCH works in older versions and can also look in any direction
  - Keywords (grading without AI): VLOOKUP, XLOOKUP, INDEX, MATCH, exact match
- [ ] **Brief, hard:** How would you clean a messy data set in Excel?
  - Key fact: remove duplicates and fix extra spaces with TRIM
  - Key fact: standardise formats such as dates and text case, and split columns where needed
  - Key fact: use Power Query so the same cleaning steps can be repeated on new data
  - Keywords (grading without AI): duplicates, TRIM, format, Power Query, repeat
- [ ] **Brief, hard:** How would you build a reliable Excel model that other people will use?
  - Key fact: keep inputs, calculations and outputs on separate sheets
  - Key fact: use tables and named ranges instead of hard-coded cell references
  - Key fact: check the results, protect the formulas and write down the assumptions
  - Keywords (grading without AI): inputs, named ranges, tables, protect, assumptions

## Data Visualization (bank_data.py)

- [ ] **MC, easy:** Which chart types suit comparing values across categories?
  - A. **Bar chart**
  - B. **Column chart**
  - C. **Dot plot**
  - D. A pie chart with 15 slices
- [ ] **MC, easy:** Which charts suit showing a trend over time?
  - A. **Line chart**
  - B. **Area chart**
  - C. **Sparkline**
  - D. Pie chart
- [ ] **MC, easy:** Which statements about a scatter plot are true?
  - A. **It shows the relationship between two numeric variables**
  - B. **Each point is one observation**
  - C. **It can reveal outliers**
  - D. It is best for showing parts of a whole
- [ ] **MC, medium:** Which make a chart easier to read?
  - A. **A title that states the point**
  - B. **Labelled axes with units**
  - C. **Direct labels instead of a distant legend**
  - D. 3D effects on a bar chart
- [ ] **MC, medium:** Which statements about colour in charts are true?
  - A. **A sequential palette suits ordered values**
  - B. **A diverging palette suits values above and below a midpoint**
  - C. **Red against green as the only difference fails colour-blind readers**
  - D. More colours always make a chart clearer
- [ ] **MC, medium:** Which statements about the value axis of a bar chart are true?
  - A. **It should start at zero**
  - B. **A cut-off axis can exaggerate differences**
  - C. **Sorting the bars by value helps comparison**
  - D. Bars should be ordered randomly
- [ ] **MC, medium:** Which charts show a distribution?
  - A. **Histogram**
  - B. **Box plot**
  - C. **Violin plot**
  - D. Pie chart
- [ ] **MC, hard:** Which statements about pie charts are true?
  - A. **They show parts of a whole**
  - B. **People compare angles less accurately than lengths**
  - C. **More than a few slices make them hard to read**
  - D. They are the best way to show change over time
- [ ] **MC, hard:** Which statements about small multiples are true?
  - A. **They repeat the same chart for each group**
  - B. **They share the same scales**
  - C. **They make groups easier to compare than one crowded chart**
  - D. Each panel should use a different scale on purpose
- [ ] **MC, hard:** Which statements about a box plot are true?
  - A. **The box covers the middle 50% of the data**
  - B. **The line inside the box is the median**
  - C. **Whiskers and dots show the spread and the outliers**
  - D. The edges of the box mark the mean
- [ ] **Blank, easy:** A ________ chart is best for showing how a value changes over time.  
  Accepted: line
- [ ] **Blank, easy:** A ________ shows the distribution of one numeric variable by grouping values into bins.  
  Accepted: histogram
- [ ] **Blank, easy:** The guide that explains what the colours or symbols of a chart mean is the ________.  
  Accepted: legend, key
- [ ] **Blank, medium:** A ________ plot shows the relationship between two numeric variables as points.  
  Accepted: scatter
- [ ] **Blank, medium:** In a box plot, the line inside the box marks the ________.  
  Accepted: median
- [ ] **Blank, medium:** A ________ palette uses two colours that meet at a midpoint, for values above and below it.  
  Accepted: diverging
- [ ] **Blank, medium:** A tiny line chart placed inside text or a table is called a ________.  
  Accepted: sparkline
- [ ] **Blank, hard:** Repeating the same small chart for each group with shared scales is called small ________.  
  Accepted: multiples
- [ ] **Blank, hard:** Edward Tufte's measure of how much of a chart's ink shows data is the data-ink ________.  
  Accepted: ratio
- [ ] **Blank, hard:** A chart that shows values as colours in a grid of rows and columns is a ________.  
  Accepted: heatmap, heat map
- [ ] **Brief, easy:** How do you choose the right chart for your data?
  - Key fact: start from the question: comparison, trend, distribution, relationship or part of a whole
  - Key fact: bars compare categories, lines show trends, histograms show distributions and scatter plots show relationships
  - Key fact: pick the simplest chart that answers the question clearly
  - Keywords (grading without AI): comparison, trend, distribution, relationship, bar
- [ ] **Brief, medium:** What makes a data visualization misleading?
  - Key fact: a cut-off or uneven axis that exaggerates differences
  - Key fact: 3D effects, distorted areas or too many colours
  - Key fact: a cherry-picked time range or missing context such as units and sources
  - Keywords (grading without AI): axis, truncated, 3D, context, cherry-pick
- [ ] **Brief, medium:** How do you use colour well in charts?
  - Key fact: use colour to highlight what matters and grey for the rest
  - Key fact: use sequential palettes for ordered values and diverging ones around a midpoint
  - Key fact: make sure colour is not the only cue, for colour-blind readers
  - Keywords (grading without AI): highlight, sequential, diverging, colour-blind, contrast
- [ ] **Brief, hard:** How would you design a chart for a non-technical audience?
  - Key fact: give it a title that states the main finding
  - Key fact: remove clutter such as heavy gridlines, and label the key values directly
  - Key fact: add short context, such as a comparison or a target, so the number means something
  - Keywords (grading without AI): title, clutter, label, context, audience
- [ ] **Brief, hard:** When would you use small multiples instead of one combined chart?
  - Key fact: when one chart would have too many overlapping lines or colours
  - Key fact: each group gets its own small chart with the same scales
  - Key fact: patterns and differences between the groups then become easy to compare
  - Keywords (grading without AI): small multiples, scale, overlap, compare, group

## Statistics (bank_data.py)

- [ ] **MC, easy:** Which of these measure central tendency?
  - A. **Mean**
  - B. **Median**
  - C. **Mode**
  - D. Range
- [ ] **MC, easy:** Which of these measure spread?
  - A. **Standard deviation**
  - B. **Variance**
  - C. **Interquartile range**
  - D. Mode
- [ ] **MC, easy:** Which statements about the median are true?
  - A. **It is the middle value of sorted data**
  - B. **Outliers affect it less than the mean**
  - C. **Half of the values lie above it**
  - D. It always equals the mean
- [ ] **MC, medium:** Which statements about a normal distribution are true?
  - A. **It is symmetric and bell-shaped**
  - B. **About 68% of values lie within one standard deviation of the mean**
  - C. **Its mean, median and mode are equal**
  - D. It never produces extreme values
- [ ] **MC, medium:** Which statements about correlation are true?
  - A. **It measures how strongly two variables move together**
  - B. **It ranges from -1 to 1**
  - C. **It does not prove causation**
  - D. A correlation of 0 means the variables are identical
- [ ] **MC, medium:** Which statements about a p-value are true?
  - A. **It is the chance of a result at least this extreme if the null hypothesis is true**
  - B. **A small p-value is evidence against the null hypothesis**
  - C. **0.05 is a common threshold**
  - D. It is the probability that the null hypothesis is true
- [ ] **MC, medium:** Which statements about sampling are true?
  - A. **A random sample helps avoid bias**
  - B. **A larger sample gives a more precise estimate**
  - C. **A biased sample can mislead however large it is**
  - D. A sample of your friends represents any population
- [ ] **MC, hard:** Which statements about Type I and Type II errors are true?
  - A. **A Type I error rejects a true null hypothesis**
  - B. **A Type II error fails to reject a false null hypothesis**
  - C. **A lower significance level reduces Type I errors**
  - D. Type I and Type II errors are the same thing
- [ ] **MC, hard:** Which statements about confidence intervals are true?
  - A. **A 95% interval method captures the true value in about 95% of repeated samples**
  - B. **A wider interval means more uncertainty**
  - C. **Larger samples give narrower intervals**
  - D. There is a 95% chance the true value lies in one particular computed interval
- [ ] **MC, hard:** Which statements about the central limit theorem are true?
  - A. **Sample means tend toward a normal distribution as the sample size grows**
  - B. **It holds for many populations that are not normal**
  - C. **It is why many tests can use the normal distribution**
  - D. It says every data set is normally distributed
- [ ] **Blank, easy:** The ________ is the sum of the values divided by how many there are.  
  Accepted: mean, average
- [ ] **Blank, easy:** The value that appears most often in a data set is the ________.  
  Accepted: mode
- [ ] **Blank, easy:** The middle value of sorted data is the ________.  
  Accepted: median
- [ ] **Blank, medium:** The square root of the variance is the standard ________.  
  Accepted: deviation
- [ ] **Blank, medium:** A value far away from the others in a data set is called an ________.  
  Accepted: outlier
- [ ] **Blank, medium:** The default assumption of no effect in a hypothesis test is the ________ hypothesis.  
  Accepted: null
- [ ] **Blank, medium:** Correlation does not imply ________.  
  Accepted: causation
- [ ] **Blank, hard:** Rejecting a null hypothesis that is actually true is a Type ________ error.  
  Accepted: I, 1, one
- [ ] **Blank, hard:** The theorem saying that sample means become normally distributed as samples grow is the central ________ theorem.  
  Accepted: limit
- [ ] **Blank, hard:** A range of values likely to contain the true value of a parameter is a ________ interval.  
  Accepted: confidence
- [ ] **Brief, easy:** When would you use the median instead of the mean?
  - Key fact: the median is the middle value, the mean is the arithmetic average
  - Key fact: the median is less affected by outliers and skewed data, such as incomes
  - Key fact: the mean suits symmetric data without extreme values
  - Keywords (grading without AI): median, mean, outlier, skewed, average
- [ ] **Brief, medium:** What is the difference between correlation and causation?
  - Key fact: correlation means two variables move together
  - Key fact: causation means one variable changes the other
  - Key fact: a hidden third variable or chance can create correlation, so causation needs experiments or careful analysis
  - Keywords (grading without AI): correlation, causation, confounder, experiment, coincidence
- [ ] **Brief, medium:** Explain what a p-value tells you.
  - Key fact: it is the chance of a result at least this extreme if the null hypothesis were true
  - Key fact: a small p-value, for example below 0.05, is evidence against the null hypothesis
  - Key fact: it is not the probability that the hypothesis is true, and it says nothing about the size of the effect
  - Keywords (grading without AI): p-value, null hypothesis, significance, 0.05, effect size
- [ ] **Brief, hard:** How would you design and analyse an A/B test?
  - Key fact: define one metric and a hypothesis before starting, and choose the sample size for enough power
  - Key fact: split users randomly into control and variant and run the test for the planned time
  - Key fact: compare the groups with a suitable test and report the effect size and confidence interval, not only the p-value
  - Keywords (grading without AI): hypothesis, random, sample size, control, confidence interval
- [ ] **Brief, hard:** Explain Type I and Type II errors with an example.
  - Key fact: a Type I error is a false positive: rejecting a true null hypothesis
  - Key fact: a Type II error is a false negative: missing a real effect
  - Key fact: a medical test with a Type I error flags a healthy person, with a Type II error misses a sick one, and lowering one usually raises the other
  - Keywords (grading without AI): Type I, Type II, false positive, false negative, power

## Data Cleaning (bank_data.py)

- [ ] **MC, easy:** Which of these are common data quality problems?
  - A. **Missing values**
  - B. **Duplicate rows**
  - C. **Inconsistent formats, such as dates**
  - D. Clear column names
- [ ] **MC, easy:** Which are ways to handle missing values?
  - A. **Remove the rows that have them**
  - B. **Fill them with a value such as the median**
  - C. **Flag them in an extra column**
  - D. Always pretend they are not there
- [ ] **MC, easy:** Which statements about duplicate rows are true?
  - A. **They can count the same record twice**
  - B. **They should be checked on the columns that identify a record**
  - C. **Removing them can change totals**
  - D. They never affect an analysis
- [ ] **MC, medium:** Which statements about outliers are true?
  - A. **They can be data entry errors**
  - B. **They can be real but rare values**
  - C. **The IQR rule flags values far outside the middle 50%**
  - D. They should always be deleted
- [ ] **MC, medium:** Which are signs that text data needs cleaning?
  - A. **Extra spaces at the start or end**
  - B. **The same city spelled several ways**
  - C. **Mixed upper and lower case for one category**
  - D. One consistent value throughout
- [ ] **MC, medium:** Which statements about data types are true?
  - A. **Numbers stored as text cannot be summed correctly**
  - B. **Dates stored as text sort in the wrong order**
  - C. **Converting types early prevents later errors**
  - D. Types never matter in an analysis
- [ ] **MC, medium:** Which statements about imputation are true?
  - A. **It fills missing values with estimates**
  - B. **Median imputation is robust to outliers**
  - C. **It can hide how much data was missing**
  - D. It always improves accuracy
- [ ] **MC, hard:** Which statements about kinds of missing data are true?
  - A. **Missing completely at random does not depend on any values**
  - B. **Missing at random depends on other observed variables**
  - C. **Missing not at random depends on the missing value itself**
  - D. Any missing data can safely be dropped
- [ ] **MC, hard:** Which help make data cleaning reproducible?
  - A. **Writing the steps as code or recorded queries**
  - B. **Keeping the raw data unchanged**
  - C. **Logging how many rows each step changes**
  - D. Editing the source file by hand
- [ ] **MC, hard:** Which checks catch data problems early?
  - A. **Validation rules such as allowed ranges**
  - B. **Checking that keys are unique**
  - C. **Comparing row counts with the source**
  - D. Trusting that the source is always right
- [ ] **Blank, easy:** Rows that appear more than once are called ________.  
  Accepted: duplicates, duplicate rows
- [ ] **Blank, easy:** Empty cells with no data are called ________ values.  
  Accepted: missing, null
- [ ] **Blank, easy:** Removing spaces at the start and end of text is called ________.  
  Accepted: trimming, trim
- [ ] **Blank, medium:** Filling missing values with estimates such as the median is called ________.  
  Accepted: imputation
- [ ] **Blank, medium:** A value far from the rest of the data is an ________.  
  Accepted: outlier
- [ ] **Blank, medium:** Making equal values look the same, such as one format for all dates, is called ________.  
  Accepted: standardization, standardisation
- [ ] **Blank, medium:** A common outlier test flags values more than 1.5 times the ________ beyond the quartiles.  
  Accepted: IQR, interquartile range
- [ ] **Blank, hard:** Data that is missing because of the missing value itself is missing not at ________.  
  Accepted: random
- [ ] **Blank, hard:** Checking data against rules such as allowed ranges and formats is called data ________.  
  Accepted: validation
- [ ] **Blank, hard:** A record of where data came from and how it changed is called data ________.  
  Accepted: lineage, provenance
- [ ] **Brief, easy:** What are the first steps you take when cleaning a new data set?
  - Key fact: look at the structure, the types and a sample of rows
  - Key fact: count missing values, duplicates and unusual values per column
  - Key fact: decide how to handle each problem, and keep the raw data unchanged
  - Keywords (grading without AI): types, missing, duplicates, profile, raw data
- [ ] **Brief, medium:** How do you decide how to handle missing values?
  - Key fact: find out why the values are missing and how many there are
  - Key fact: drop rows or columns when the missing share is small and random
  - Key fact: otherwise fill them, for example with the median or a model, and flag which values were filled
  - Keywords (grading without AI): missing, drop, impute, median, flag
- [ ] **Brief, medium:** How do you deal with outliers?
  - Key fact: find them with plots or rules such as the IQR or z-scores
  - Key fact: check whether they are errors or real but rare values
  - Key fact: fix or remove errors, and keep real values or use methods that are robust to them
  - Keywords (grading without AI): outlier, IQR, z-score, error, robust
- [ ] **Brief, hard:** How would you make a data cleaning process reproducible?
  - Key fact: write the cleaning steps as code or recorded transformations, not manual edits
  - Key fact: keep the raw data unchanged and save the cleaned output separately
  - Key fact: add checks such as row counts and validation rules, and log what each step changed
  - Keywords (grading without AI): code, raw data, validation, log, reproducible
- [ ] **Brief, hard:** How would you clean inconsistent category values, such as one city written several ways?
  - Key fact: trim spaces and use one letter case
  - Key fact: map the variants to one standard value with a lookup table
  - Key fact: check the remaining distinct values and add a rule that catches new variants
  - Keywords (grading without AI): trim, case, mapping, standard, distinct

## Dashboards (bank_data.py)

- [ ] **MC, easy:** Which make a dashboard useful?
  - A. **A clear purpose for a specific audience**
  - B. **The most important numbers at the top**
  - C. **Consistent colours and labels**
  - D. As many charts as will fit
- [ ] **MC, easy:** Which statements about KPIs are true?
  - A. **They measure progress toward a goal**
  - B. **They should be few and clearly defined**
  - C. **They often show a comparison with a target**
  - D. Every metric is a KPI
- [ ] **MC, easy:** Which help a user read a dashboard quickly?
  - A. **Large numbers for the main KPIs**
  - B. **Grouping related charts**
  - C. **Short, clear titles**
  - D. Decorative 3D charts
- [ ] **MC, medium:** Which statements about dashboard filters are true?
  - A. **They let users focus on a region or a date range**
  - B. **Showing which filters are on avoids confusion**
  - C. **Too many filters can confuse users**
  - D. Filters change the source data
- [ ] **MC, medium:** Which statements about dashboard layout are true?
  - A. **Users look at the top left first**
  - B. **The main KPIs belong at the top**
  - C. **Details can sit lower or in drill-downs**
  - D. The layout does not affect understanding
- [ ] **MC, medium:** Which statements about refresh frequency are true?
  - A. **It should match how fast decisions are made**
  - B. **Real-time data costs more to run**
  - C. **Showing the last update time builds trust**
  - D. Every dashboard needs live data
- [ ] **MC, medium:** Which statements about operational and strategic dashboards are true?
  - A. **Operational dashboards track current activity**
  - B. **Strategic dashboards show long-term trends**
  - C. **They need different refresh rates**
  - D. They should always show the same metrics
- [ ] **MC, hard:** Which help a dashboard load fast?
  - A. **Aggregating data in advance**
  - B. **Fewer visuals per page**
  - C. **Filtering data at the source**
  - D. Loading every raw row when it opens
- [ ] **MC, hard:** Which statements about drill-down are true?
  - A. **It takes users from a summary to the detail**
  - B. **It keeps the main view simple**
  - C. **A hierarchy such as year, quarter and month supports it**
  - D. It removes the need for a summary
- [ ] **MC, hard:** Which help users trust a dashboard?
  - A. **Clear metric definitions**
  - B. **The data source and the last update time**
  - C. **Numbers that match other trusted reports**
  - D. Hiding how the numbers are calculated
- [ ] **Blank, easy:** A ________ is a key number that measures progress toward a goal.  
  Accepted: KPI, key performance indicator
- [ ] **Blank, easy:** A control that lets users show only part of the data, such as one region, is a ________.  
  Accepted: filter, slicer
- [ ] **Blank, easy:** A single big number on a dashboard is often shown on a KPI ________.  
  Accepted: card, tile
- [ ] **Blank, medium:** Moving from a summary to more detailed data is called drill-________.  
  Accepted: down
- [ ] **Blank, medium:** A dashboard that tracks day-to-day activity is an ________ dashboard.  
  Accepted: operational
- [ ] **Blank, medium:** Showing the time of the last data refresh helps users ________ the numbers.  
  Accepted: trust
- [ ] **Blank, medium:** Computing totals in advance so a dashboard loads faster is called pre-________.  
  Accepted: aggregation, aggregating
- [ ] **Blank, hard:** A dashboard that shows long-term trends for leaders is a ________ dashboard.  
  Accepted: strategic
- [ ] **Blank, hard:** Limiting which rows each user can see in a dashboard is called row-level ________.  
  Accepted: security
- [ ] **Blank, hard:** A written meaning for each metric, kept in one shared place, is a metric ________.  
  Accepted: definition, glossary, dictionary
- [ ] **Brief, easy:** What makes a good dashboard?
  - Key fact: it has a clear purpose and a specific audience
  - Key fact: it shows a few key metrics at the top, with context such as targets or trends
  - Key fact: it uses simple, consistent charts and avoids clutter
  - Keywords (grading without AI): purpose, audience, KPI, context, clutter
- [ ] **Brief, medium:** How do you choose which metrics go on a dashboard?
  - Key fact: start from the decisions the audience needs to make
  - Key fact: pick a few KPIs that measure progress toward the goal
  - Key fact: define each metric clearly and add comparisons such as the last period or a target
  - Keywords (grading without AI): decision, KPI, goal, definition, comparison
- [ ] **Brief, medium:** How would you lay out a dashboard?
  - Key fact: put the most important KPIs at the top left, where people look first
  - Key fact: group related charts and go from summary to detail
  - Key fact: use consistent colours, labels and spacing, and leave details for drill-downs
  - Keywords (grading without AI): layout, top left, group, consistent, drill-down
- [ ] **Brief, hard:** A dashboard is slow. How would you speed it up?
  - Key fact: reduce the data: aggregate in advance and filter at the source
  - Key fact: reduce the work: fewer visuals per page and simpler calculations
  - Key fact: check the data model and queries, and use extracts or caching where the tool allows
  - Keywords (grading without AI): aggregate, filter, visuals, data model, cache
- [ ] **Brief, hard:** How do you make sure people trust and use a dashboard?
  - Key fact: define every metric clearly and make the numbers match other trusted reports
  - Key fact: show the data source and the time of the last refresh
  - Key fact: involve users early, collect feedback and remove what nobody uses
  - Keywords (grading without AI): definition, source, refresh, feedback, adoption

## Power BI (bank_data.py)

- [ ] **MC, easy:** Which of these are parts of Power BI?
  - A. **Power BI Desktop**
  - B. **Power BI Service**
  - C. **Power Query**
  - D. Power Point
- [ ] **MC, easy:** Which statements about Power BI visuals are true?
  - A. **You build them by dragging fields onto the canvas**
  - B. **Slicers filter other visuals**
  - C. **Clicking one visual can filter the others**
  - D. A visual cannot be changed after it is created
- [ ] **MC, easy:** Which can Power BI connect to?
  - A. **Excel files**
  - B. **SQL databases**
  - C. **Web APIs**
  - D. Only text files
- [ ] **MC, medium:** Which statements about Power Query in Power BI are true?
  - A. **It cleans and shapes data before loading**
  - B. **Each step is recorded and can be changed**
  - C. **It uses the M language**
  - D. It writes DAX measures
- [ ] **MC, medium:** Which statements about a star schema in Power BI are true?
  - A. **Fact tables hold numbers such as sales**
  - B. **Dimension tables hold descriptions such as products and dates**
  - C. **Dimensions filter facts through relationships**
  - D. One flat table is always better
- [ ] **MC, medium:** Which statements about measures and calculated columns are true?
  - A. **A measure is calculated at query time for the current filters**
  - B. **A calculated column is stored for every row**
  - C. **Measures suit totals and ratios**
  - D. Calculated columns use less memory than measures
- [ ] **MC, medium:** Which DAX functions aggregate values?
  - A. **SUM**
  - B. **AVERAGE**
  - C. **COUNTROWS**
  - D. FILTERX
- [ ] **MC, hard:** Which statements about CALCULATE in DAX are true?
  - A. **It evaluates an expression in a changed filter context**
  - B. **It can add or remove filters**
  - C. **Most advanced measures use it**
  - D. It works only on text columns
- [ ] **MC, hard:** Which statements about row context and filter context are true?
  - A. **Calculated columns are evaluated in row context**
  - B. **Measures are evaluated in filter context**
  - C. **CALCULATE turns row context into filter context**
  - D. They are the same thing
- [ ] **MC, hard:** Which statements about row-level security in Power BI are true?
  - A. **It limits the rows a user can see**
  - B. **Roles are defined with DAX filters**
  - C. **It is enforced in the Power BI Service**
  - D. It hides whole reports from every user
- [ ] **Blank, easy:** Power BI's free desktop app for building reports is Power BI ________.  
  Accepted: Desktop
- [ ] **Blank, easy:** A visual that lets users filter the other visuals on a page is a ________.  
  Accepted: slicer
- [ ] **Blank, easy:** The formula language for measures in Power BI is ________.  
  Accepted: DAX
- [ ] **Blank, medium:** Power Query records its steps in the ________ language.  
  Accepted: M
- [ ] **Blank, medium:** In a star schema, the central table holding numbers such as sales amounts is the ________ table.  
  Accepted: fact
- [ ] **Blank, medium:** A DAX ________ is calculated at query time and responds to filters.  
  Accepted: measure
- [ ] **Blank, medium:** A table of descriptions, such as products or dates, is a ________ table.  
  Accepted: dimension
- [ ] **Blank, hard:** The DAX function ________ evaluates an expression in a modified filter context.  
  Accepted: CALCULATE
- [ ] **Blank, hard:** Restricting which rows each user can see in Power BI is row-level ________.  
  Accepted: security
- [ ] **Blank, hard:** Time intelligence functions such as TOTALYTD need a ________ table marked in the model.  
  Accepted: date, calendar
- [ ] **Brief, easy:** What is Power BI used for?
  - Key fact: it connects to data sources and builds interactive reports and dashboards
  - Key fact: Power Query cleans the data and DAX adds calculations
  - Key fact: reports are shared through the Power BI Service with scheduled refresh and access control
  - Keywords (grading without AI): reports, Power Query, DAX, Service, interactive
- [ ] **Brief, medium:** What is the difference between a measure and a calculated column in Power BI?
  - Key fact: a calculated column is computed for each row at refresh and stored in the model
  - Key fact: a measure is computed at query time for the current filters
  - Key fact: use measures for totals, ratios and other aggregations, and columns for row-level attributes such as categories
  - Keywords (grading without AI): measure, calculated column, filter context, row, storage
- [ ] **Brief, medium:** Why is a star schema recommended in Power BI?
  - Key fact: fact tables hold the numbers and dimension tables hold the descriptions
  - Key fact: dimensions filter facts through one-to-many relationships
  - Key fact: it keeps DAX simpler, the model smaller and reports faster than one wide table
  - Keywords (grading without AI): star schema, fact, dimension, relationship, performance
- [ ] **Brief, hard:** Explain filter context and CALCULATE in DAX.
  - Key fact: filter context is the set of filters from slicers, rows and columns that applies to a measure
  - Key fact: CALCULATE evaluates an expression with filters added, removed or replaced
  - Key fact: for example, sales for the same period last year or a share of the total both use CALCULATE
  - Keywords (grading without AI): filter context, CALCULATE, ALL, measure, time intelligence
- [ ] **Brief, hard:** How would you make a slow Power BI report faster?
  - Key fact: shrink the model: remove unused columns, lower cardinality and use a star schema
  - Key fact: write efficient DAX and prefer measures over complex calculated columns
  - Key fact: use fewer visuals per page and check the report with Performance Analyzer
  - Keywords (grading without AI): model size, star schema, DAX, Performance Analyzer, visuals

## Tableau (bank_data.py)

- [ ] **MC, easy:** Which of these are Tableau products?
  - A. **Tableau Desktop**
  - B. **Tableau Server**
  - C. **Tableau Public**
  - D. Tableau Excelsior
- [ ] **MC, easy:** Which statements about dimensions and measures in Tableau are true?
  - A. **Dimensions are usually categories such as region**
  - B. **Measures are usually numbers such as sales**
  - C. **Measures are aggregated by default**
  - D. Dimensions are always summed
- [ ] **MC, easy:** Which can you build in Tableau?
  - A. **Worksheets**
  - B. **Dashboards**
  - C. **Stories**
  - D. Database tables
- [ ] **MC, medium:** Which statements about Tableau filters are true?
  - A. **Extract filters reduce the data in an extract**
  - B. **Context filters run before other filters**
  - C. **Quick filters let users pick values**
  - D. All filters run at the same time
- [ ] **MC, medium:** Which statements about live connections and extracts are true?
  - A. **A live connection queries the source each time**
  - B. **An extract stores a snapshot for speed**
  - C. **Extracts can be refreshed on a schedule**
  - D. Extracts always show real-time data
- [ ] **MC, medium:** Which statements about calculated fields are true?
  - A. **They create new fields from formulas**
  - B. **They can use IF and CASE logic**
  - C. **They can aggregate with SUM or AVG**
  - D. They change the source database
- [ ] **MC, medium:** Which statements about discrete and continuous fields are true?
  - A. **Discrete fields are blue and create headers**
  - B. **Continuous fields are green and create axes**
  - C. **A date can be used either way**
  - D. Measures are always discrete
- [ ] **MC, hard:** Which statements about level of detail expressions are true?
  - A. **FIXED computes at a set level whatever the view shows**
  - B. **INCLUDE adds a dimension to the view's level**
  - C. **EXCLUDE removes a dimension from it**
  - D. They work only in Excel
- [ ] **MC, hard:** Which statements about table calculations are true?
  - A. **They compute on the results in the view**
  - B. **Running total and percent of total are examples**
  - C. **Their result depends on the direction they compute along**
  - D. They run in the source database
- [ ] **MC, hard:** Which statements about relationships and joins in Tableau are true?
  - A. **Relationships keep tables separate and join them as needed**
  - B. **Joins merge tables into one before analysis**
  - C. **Joins can duplicate rows and inflate totals**
  - D. Relationships always duplicate rows
- [ ] **Blank, easy:** In Tableau, numeric fields that are aggregated, such as sales, are called ________.  
  Accepted: measures
- [ ] **Blank, easy:** In Tableau, categorical fields such as region are called ________.  
  Accepted: dimensions
- [ ] **Blank, easy:** Tableau's free version for sharing work publicly is Tableau ________.  
  Accepted: Public
- [ ] **Blank, medium:** A saved snapshot of data that Tableau queries for speed is an ________.  
  Accepted: extract
- [ ] **Blank, medium:** A ________ field is created from a formula written in Tableau.  
  Accepted: calculated
- [ ] **Blank, medium:** Continuous fields in Tableau are shown in ________ and create axes.  
  Accepted: green
- [ ] **Blank, medium:** A sequence of sheets or dashboards that tells a narrative is a Tableau ________.  
  Accepted: story
- [ ] **Blank, hard:** The level of detail expression ________ computes a value at a set level whatever the view.  
  Accepted: FIXED
- [ ] **Blank, hard:** A ________ filter in Tableau runs before the other filters and limits what they see.  
  Accepted: context
- [ ] **Blank, hard:** Calculations such as running totals that work on the results in the view are ________ calculations.  
  Accepted: table
- [ ] **Brief, easy:** What is Tableau, and what is it used for?
  - Key fact: Tableau is a tool for building interactive charts and dashboards
  - Key fact: users drag dimensions and measures onto shelves to build views
  - Key fact: dashboards are shared through Tableau Server, Tableau Cloud or Tableau Public
  - Keywords (grading without AI): Tableau, dashboard, dimension, measure, interactive
- [ ] **Brief, medium:** What is the difference between a live connection and an extract in Tableau?
  - Key fact: a live connection queries the data source every time the view changes
  - Key fact: an extract stores a snapshot of the data in Tableau's fast format
  - Key fact: extracts are faster and work offline but must be refreshed, while live connections stay current but depend on the source's speed
  - Keywords (grading without AI): live connection, extract, snapshot, refresh, performance
- [ ] **Brief, medium:** What is the difference between dimensions and measures in Tableau?
  - Key fact: dimensions are categories such as region or product that slice the data
  - Key fact: measures are numbers such as sales that are aggregated
  - Key fact: dimensions create headers, measures create values or axes
  - Keywords (grading without AI): dimension, measure, aggregate, category, header
- [ ] **Brief, hard:** Explain level of detail expressions in Tableau.
  - Key fact: LOD expressions compute a value at a different level from the view
  - Key fact: FIXED uses the dimensions you name, INCLUDE adds dimensions and EXCLUDE removes them
  - Key fact: for example, FIXED by customer gives each customer's first order date for a cohort analysis
  - Keywords (grading without AI): LOD, FIXED, INCLUDE, EXCLUDE, cohort
- [ ] **Brief, hard:** How would you make a slow Tableau dashboard faster?
  - Key fact: use extracts and filter the data down to what is needed
  - Key fact: limit the number of sheets and marks on the dashboard
  - Key fact: simplify calculations, use context filters carefully and check the Performance Recorder
  - Keywords (grading without AI): extract, filter, marks, calculation, Performance Recorder

## Critical Thinking (bank_data.py)

- [ ] **Brief, easy:** A chart shows that sales rose after a new feature launched. Can you say the feature caused the rise?
  - Key fact: the rise could have other causes, such as the season, pricing or marketing
  - Key fact: check a control group, or compare periods and segments without the feature
  - Key fact: say what the data does and does not show before drawing a conclusion
  - Keywords (grading without AI): cause, correlation, control group, season, evidence
- [ ] **Brief, medium:** Someone asks you for a number that comes out surprisingly high. What do you do?
  - Key fact: check the data source, filters and definitions before sharing it
  - Key fact: compare it with past values or another source to see whether it is plausible
  - Key fact: ask how it will be used and explain any doubt clearly
  - Keywords (grading without AI): verify, source, definition, compare, plausible
- [ ] **Brief, medium:** How do you approach a problem that is not clearly defined?
  - Key fact: ask questions to understand the goal and who needs the answer
  - Key fact: break the problem into smaller questions and list your assumptions
  - Key fact: test the assumptions with data or people before investing a lot of work
  - Keywords (grading without AI): goal, questions, assumptions, break down, validate
- [ ] **Brief, hard:** Two teams give you different numbers for the same metric. How do you resolve it?
  - Key fact: compare how each team defines the metric, including filters and time ranges
  - Key fact: trace both numbers back to their sources and calculations
  - Key fact: agree on one definition with the stakeholders and write it down
  - Keywords (grading without AI): definition, source, reconcile, stakeholder, document
- [ ] **Brief, hard:** How do you avoid bias in your own analysis?
  - Key fact: state the hypothesis and the method before looking at the results
  - Key fact: look for evidence against your idea, not only for it
  - Key fact: check the data for sampling bias and ask someone else to review the conclusion
  - Keywords (grading without AI): bias, hypothesis, evidence, sampling, review

## Communication (bank_data.py)

- [ ] **Brief, easy:** How do you explain a technical result to a non-technical audience?
  - Key fact: start with the main finding and why it matters to them
  - Key fact: use plain words, a simple chart or an example instead of jargon
  - Key fact: check that they understood and invite questions
  - Keywords (grading without AI): plain language, main point, example, audience, questions
- [ ] **Brief, medium:** How do you write a clear status update?
  - Key fact: lead with what is done, what comes next and any blockers
  - Key fact: keep it short and specific, with dates or numbers where possible
  - Key fact: flag risks early and say what help you need
  - Keywords (grading without AI): status, blocker, risk, specific, next steps
- [ ] **Brief, medium:** How do you handle a disagreement with a stakeholder about a result?
  - Key fact: listen to understand their concern and what they expected
  - Key fact: show the data and the method behind the result openly
  - Key fact: agree on a next step, such as a further check, instead of arguing
  - Keywords (grading without AI): listen, data, method, agree, next step
- [ ] **Brief, hard:** How do you present findings so that they lead to a decision?
  - Key fact: state the recommendation and the decision needed at the start
  - Key fact: support it with the few most important facts and their limits
  - Key fact: show the options and their trade-offs, and say what happens next
  - Keywords (grading without AI): recommendation, decision, evidence, trade-off, next steps
- [ ] **Brief, hard:** How do you make sure a written document works for different readers?
  - Key fact: know the readers and what each of them needs from the document
  - Key fact: put a short summary first, then the details in clear sections
  - Key fact: use plain language, define terms and ask someone to review it
  - Keywords (grading without AI): audience, summary, structure, plain language, review

## Pandas (bank_data.py)

- [ ] **MC, easy:** Which of these are core pandas data structures?
  - A. **DataFrame**
  - B. **Series**
  - C. **Index**
  - D. Matrix
- [ ] **MC, easy:** Which pandas functions read data from files?
  - A. **read_csv**
  - B. **read_excel**
  - C. **read_json**
  - D. read_html_css
- [ ] **MC, easy:** Which methods give a quick look at a DataFrame?
  - A. **head**
  - B. **info**
  - C. **describe**
  - D. drop
- [ ] **MC, medium:** Which statements about loc and iloc are true?
  - A. **loc selects by labels**
  - B. **iloc selects by integer positions**
  - C. **loc slices include the end label**
  - D. iloc slices include the end position
- [ ] **MC, medium:** Which statements about missing values in pandas are true?
  - A. **isna finds missing values**
  - B. **fillna fills them**
  - C. **dropna removes rows or columns that have them**
  - D. NaN equals NaN
- [ ] **MC, medium:** Which statements about groupby are true?
  - A. **It splits data into groups by keys**
  - B. **It applies an aggregation such as sum to each group**
  - C. **agg can apply several aggregations at once**
  - D. It changes the original DataFrame in place
- [ ] **MC, medium:** Which statements about merge in pandas are true?
  - A. **It joins DataFrames on key columns**
  - B. **how can be inner, left, right or outer**
  - C. **Duplicate keys can multiply rows**
  - D. It only stacks rows on top of each other
- [ ] **MC, hard:** Which make pandas code faster?
  - A. **Vectorized operations instead of row loops**
  - B. **The category dtype for repeated strings**
  - C. **Avoiding apply with Python functions on large data**
  - D. Looping with iterrows over millions of rows
- [ ] **MC, hard:** Which statements about SettingWithCopyWarning are true?
  - A. **It warns that you may be changing a copy, not the original**
  - B. **Chained indexing such as df[a][b] = x can cause it**
  - C. **Doing the assignment with .loc avoids it**
  - D. It means the data was saved to disk
- [ ] **MC, hard:** Which statements about pivot_table and melt are true?
  - A. **pivot_table turns rows into columns with an aggregation**
  - B. **melt turns columns into rows**
  - C. **They reshape between wide and long formats**
  - D. They delete data
- [ ] **Blank, easy:** The function pd.________() loads a CSV file into a DataFrame.  
  Accepted: read_csv
- [ ] **Blank, easy:** A single column of a DataFrame is a pandas ________.  
  Accepted: Series
- [ ] **Blank, easy:** The method df.________() shows the first five rows.  
  Accepted: head
- [ ] **Blank, medium:** The method df.________() returns summary statistics such as the mean and quartiles.  
  Accepted: describe
- [ ] **Blank, medium:** The method df.________() fills missing values.  
  Accepted: fillna
- [ ] **Blank, medium:** df.________('city') splits the data into groups by city.  
  Accepted: groupby
- [ ] **Blank, medium:** The indexer df.________ selects rows and columns by integer position.  
  Accepted: iloc
- [ ] **Blank, hard:** The function pd.________() joins two DataFrames on key columns, like a SQL join.  
  Accepted: merge
- [ ] **Blank, hard:** The method df.________() turns wide columns into long rows.  
  Accepted: melt
- [ ] **Blank, hard:** The pandas dtype ________ saves memory for columns with a few repeated string values.  
  Accepted: category, categorical
- [ ] **Brief, easy:** What is a pandas DataFrame?
  - Key fact: a DataFrame is a table of rows and labelled columns
  - Key fact: each column is a Series and can have its own type
  - Key fact: it supports filtering, grouping, joining and reshaping data
  - Keywords (grading without AI): DataFrame, Series, columns, index, table
- [ ] **Brief, medium:** What is the difference between loc and iloc?
  - Key fact: loc selects rows and columns by their labels
  - Key fact: iloc selects them by integer position
  - Key fact: loc slices include the end label, while iloc slices leave out the end position
  - Keywords (grading without AI): loc, iloc, label, position, slice
- [ ] **Brief, medium:** How do you handle missing data in pandas?
  - Key fact: find it with isna().sum() for each column
  - Key fact: drop rows or columns with dropna when the missing share is small
  - Key fact: fill values with fillna, for example the median or the previous value, depending on the data
  - Keywords (grading without AI): isna, dropna, fillna, median, ffill
- [ ] **Brief, hard:** How would you speed up slow pandas code?
  - Key fact: replace row loops and apply with vectorized operations
  - Key fact: use smaller dtypes such as category, and downcast numbers to save memory
  - Key fact: read only the columns you need, and for very large data use chunks or tools such as Polars or Spark
  - Keywords (grading without AI): vectorized, apply, dtype, category, chunks
- [ ] **Brief, hard:** Explain groupby in pandas with an example.
  - Key fact: groupby splits the data into groups by one or more keys
  - Key fact: an aggregation such as sum, mean or count is applied to each group
  - Key fact: for example, df.groupby('region')['sales'].sum() gives total sales per region, and agg applies several functions at once
  - Keywords (grading without AI): groupby, split, aggregate, agg, transform

## Scikit-learn (bank_data.py)

- [ ] **MC, easy:** Which methods do most scikit-learn estimators have?
  - A. **fit**
  - B. **predict**
  - C. **score**
  - D. compile
- [ ] **MC, easy:** Which of these are scikit-learn modules?
  - A. **sklearn.model_selection**
  - B. **sklearn.preprocessing**
  - C. **sklearn.metrics**
  - D. sklearn.tensors
- [ ] **MC, easy:** Which statements about train_test_split are true?
  - A. **It splits data into training and test sets**
  - B. **test_size sets the share of the test set**
  - C. **random_state makes the split repeatable**
  - D. It trains the model
- [ ] **MC, medium:** Which statements about transformers such as StandardScaler are true?
  - A. **fit learns parameters such as the mean**
  - B. **transform applies them**
  - C. **fit_transform does both**
  - D. They should be fit on the test set
- [ ] **MC, medium:** Which statements about Pipeline are true?
  - A. **It chains preprocessing steps and a model**
  - B. **It prevents leakage during cross-validation**
  - C. **It can be tuned with GridSearchCV**
  - D. It can hold only one step
- [ ] **MC, medium:** Which statements about GridSearchCV are true?
  - A. **It tries every combination in a parameter grid**
  - B. **It cross-validates each combination**
  - C. **best_params_ shows the best settings**
  - D. It is faster than random search on large grids
- [ ] **MC, medium:** Which statements about ColumnTransformer are true?
  - A. **It applies different transformations to different columns**
  - B. **It can scale numbers and one-hot encode categories in one step**
  - C. **It fits inside a Pipeline**
  - D. It works only on text
- [ ] **MC, hard:** Which statements about stratified splitting are true?
  - A. **It keeps the class proportions in each split**
  - B. **It matters for imbalanced data**
  - C. **StratifiedKFold does it for cross-validation**
  - D. It shuffles the labels randomly
- [ ] **MC, hard:** Which statements about class_weight are true?
  - A. **balanced weights classes by their inverse frequency**
  - B. **It helps with imbalanced data**
  - C. **Many classifiers support it**
  - D. It removes the minority class
- [ ] **MC, hard:** Which statements about saving scikit-learn models are true?
  - A. **joblib can save and load a fitted pipeline**
  - B. **The same library versions should be used to load it**
  - C. **Saving the whole pipeline keeps the preprocessing with the model**
  - D. Saving only the model also saves the scaler
- [ ] **Blank, easy:** In scikit-learn, the method ________() trains a model on data.  
  Accepted: fit
- [ ] **Blank, easy:** The function train_test_________() divides data into training and test sets.  
  Accepted: split
- [ ] **Blank, easy:** The method ________() returns the model's outputs for new data.  
  Accepted: predict
- [ ] **Blank, medium:** The ________ class chains preprocessing steps and a model into one object.  
  Accepted: Pipeline
- [ ] **Blank, medium:** StandardScaler's method fit_________() learns the scaling and applies it in one call.  
  Accepted: transform
- [ ] **Blank, medium:** ________ tries every combination of hyperparameters with cross-validation.  
  Accepted: GridSearchCV
- [ ] **Blank, medium:** The function cross_val_________() returns one score for each cross-validation fold.  
  Accepted: score
- [ ] **Blank, hard:** ________ applies different preprocessing to different columns.  
  Accepted: ColumnTransformer
- [ ] **Blank, hard:** Setting class_weight to ________ weights classes by their inverse frequency.  
  Accepted: balanced
- [ ] **Blank, hard:** The method ________() returns class probabilities instead of labels.  
  Accepted: predict_proba
- [ ] **Brief, easy:** What is the usual workflow for training a model in scikit-learn?
  - Key fact: split the data with train_test_split
  - Key fact: create an estimator, call fit on the training data and predict on the test data
  - Key fact: measure the result with a metric from sklearn.metrics
  - Keywords (grading without AI): train_test_split, fit, predict, metrics, estimator
- [ ] **Brief, medium:** Why use a Pipeline in scikit-learn?
  - Key fact: it chains preprocessing and the model into one object
  - Key fact: preprocessing is fit only on the training folds, which prevents data leakage in cross-validation
  - Key fact: the whole pipeline can be tuned, saved and used for prediction in one step
  - Keywords (grading without AI): Pipeline, leakage, cross-validation, preprocessing, tuning
- [ ] **Brief, medium:** How do you tune hyperparameters in scikit-learn?
  - Key fact: GridSearchCV tries every combination in a grid with cross-validation
  - Key fact: RandomizedSearchCV samples combinations and suits large search spaces
  - Key fact: read best_params_ and best_score_, then check the final model on the test set
  - Keywords (grading without AI): GridSearchCV, RandomizedSearchCV, cross-validation, best_params_, test set
- [ ] **Brief, hard:** How would you preprocess a data set with numeric and categorical columns in scikit-learn?
  - Key fact: use a ColumnTransformer to treat the column types differently
  - Key fact: impute and scale the numeric columns, impute and one-hot encode the categorical ones
  - Key fact: put it in a Pipeline with the model so it is fit only on the training data
  - Keywords (grading without AI): ColumnTransformer, SimpleImputer, StandardScaler, OneHotEncoder, Pipeline
- [ ] **Brief, hard:** How do you handle an imbalanced classification problem in scikit-learn?
  - Key fact: use stratified splits so each fold keeps the class balance
  - Key fact: use class_weight set to balanced, or resample, for example with imbalanced-learn
  - Key fact: evaluate with precision, recall, F1 or PR AUC and tune the decision threshold
  - Keywords (grading without AI): stratified, class_weight, resampling, F1, threshold

## ETL (bank_dataeng.py)

- [ ] **MC, easy:** Which are the steps of ETL?
  - A. **Extract**
  - B. **Transform**
  - C. **Load**
  - D. Encrypt
- [ ] **MC, easy:** Which are common data sources for ETL?
  - A. **Databases**
  - B. **APIs**
  - C. **CSV files**
  - D. A monitor screen
- [ ] **MC, easy:** Which of these are transformations?
  - A. **Converting date formats**
  - B. **Removing duplicates**
  - C. **Joining tables**
  - D. Copying files without any change
- [ ] **MC, medium:** Which statements about ETL and ELT are true?
  - A. **ETL transforms data before loading it**
  - B. **ELT loads raw data first and transforms it in the warehouse**
  - C. **Cloud warehouses made ELT common**
  - D. They are the same process
- [ ] **MC, medium:** Which statements about incremental loads are true?
  - A. **They load only new or changed rows**
  - B. **They are faster than full reloads**
  - C. **A timestamp column or change data capture finds the changed rows**
  - D. They always load the whole table
- [ ] **MC, medium:** Which statements about idempotent ETL jobs are true?
  - A. **Running them twice gives the same result**
  - B. **They make retries safe**
  - C. **Upserts or overwriting a partition help**
  - D. They append duplicates on each run
- [ ] **MC, medium:** Which help find errors in an ETL job?
  - A. **Comparing row counts between source and target**
  - B. **Null and range checks on key columns**
  - C. **Logging each step**
  - D. Hiding the failed rows
- [ ] **MC, hard:** Which statements about change data capture are true?
  - A. **It reads inserts, updates and deletes from the source**
  - B. **Log-based CDC reads the database's transaction log**
  - C. **It allows near real-time pipelines**
  - D. It needs a full table scan every time
- [ ] **MC, hard:** Which statements about slowly changing dimensions are true?
  - A. **Type 1 overwrites old values**
  - B. **Type 2 keeps history with new rows and validity dates**
  - C. **Type 2 needs a surrogate key**
  - D. Type 1 keeps the full history
- [ ] **MC, hard:** Which help an ETL job deal with bad records?
  - A. **Sending them to a quarantine or error table**
  - B. **Alerting when the error rate is high**
  - C. **Validating the schema at the start**
  - D. Stopping the whole warehouse forever
- [ ] **Blank, easy:** ETL stands for Extract, Transform and ________.  
  Accepted: Load
- [ ] **Blank, easy:** The step that reads data from the source systems is called ________.  
  Accepted: extract, extraction
- [ ] **Blank, easy:** Loading data into the warehouse first and transforming it there is called ________.  
  Accepted: ELT
- [ ] **Blank, medium:** Loading only the rows that are new or changed is an ________ load.  
  Accepted: incremental
- [ ] **Blank, medium:** An insert that updates the row if it already exists is called an ________.  
  Accepted: upsert, merge
- [ ] **Blank, medium:** A job that gives the same result when it runs twice is ________.  
  Accepted: idempotent
- [ ] **Blank, medium:** A temporary area where raw data lands before it is transformed is a ________ area.  
  Accepted: staging
- [ ] **Blank, hard:** Reading changes from a database's transaction log is called change data ________.  
  Accepted: capture
- [ ] **Blank, hard:** A Type ________ slowly changing dimension keeps history by adding a new row for each change.  
  Accepted: 2, II, two
- [ ] **Blank, hard:** A generated key that a warehouse uses instead of the source system's key is a ________ key.  
  Accepted: surrogate
- [ ] **Brief, easy:** What is ETL?
  - Key fact: Extract reads data from sources such as databases, APIs and files
  - Key fact: Transform cleans, joins and reshapes the data
  - Key fact: Load writes the result into a target such as a data warehouse
  - Keywords (grading without AI): extract, transform, load, warehouse, source
- [ ] **Brief, medium:** What is the difference between ETL and ELT?
  - Key fact: ETL transforms the data before loading it into the target
  - Key fact: ELT loads the raw data first and transforms it inside the warehouse
  - Key fact: ELT suits cloud warehouses with cheap storage and strong compute, and keeps the raw data for reprocessing
  - Keywords (grading without AI): ETL, ELT, warehouse, raw data, transform
- [ ] **Brief, medium:** How do you make an ETL job safe to rerun?
  - Key fact: make it idempotent, so a rerun gives the same result
  - Key fact: use upserts or overwrite a whole partition instead of plain appends
  - Key fact: track which batches were processed, so a retry does not duplicate data
  - Keywords (grading without AI): idempotent, upsert, partition, retry, duplicate
- [ ] **Brief, hard:** How would you load only the changed data from a large source table?
  - Key fact: use an updated-at column or a watermark to select the rows changed since the last run
  - Key fact: or use log-based change data capture to read inserts, updates and deletes
  - Key fact: merge the changes into the target with upserts and handle deletes explicitly
  - Keywords (grading without AI): incremental, watermark, CDC, upsert, deletes
- [ ] **Brief, hard:** How do you handle data quality inside an ETL pipeline?
  - Key fact: validate the schema, types, nulls and ranges as data arrives
  - Key fact: send bad records to a quarantine table instead of failing silently or dropping them
  - Key fact: compare row counts and totals with the source and alert on problems
  - Keywords (grading without AI): validation, schema, quarantine, row count, alert

## Data Pipelines (bank_dataeng.py)

- [ ] **MC, easy:** Which are parts of a typical data pipeline?
  - A. **Ingestion**
  - B. **Transformation**
  - C. **Storage**
  - D. Printing
- [ ] **MC, easy:** Which statements about batch and streaming are true?
  - A. **Batch processes data in chunks at intervals**
  - B. **Streaming processes events as they arrive**
  - C. **Streaming gives lower latency**
  - D. Batch is always real time
- [ ] **MC, easy:** Which are common data pipeline tools?
  - A. **Apache Airflow**
  - B. **Apache Spark**
  - C. **Apache Kafka**
  - D. Adobe Photoshop
- [ ] **MC, medium:** Which statements about pipeline orchestration are true?
  - A. **It schedules tasks and runs them in order**
  - B. **It retries failed tasks**
  - C. **It shows task status and history**
  - D. It stores all the data
- [ ] **MC, medium:** Which make a pipeline reliable?
  - A. **Idempotent tasks**
  - B. **Retries with backoff**
  - C. **Alerts on failure**
  - D. Running it only by hand
- [ ] **MC, medium:** Which statements about partitioning stored data are true?
  - A. **Partitioning by date lets queries skip files they do not need**
  - B. **It makes reprocessing a single day easy**
  - C. **Too many tiny partitions hurt performance**
  - D. It always slows queries down
- [ ] **MC, medium:** Which statements about schema evolution are true?
  - A. **Source systems change their columns over time**
  - B. **Adding a nullable column is usually safe**
  - C. **Removing or renaming columns can break later jobs**
  - D. Schemas never change
- [ ] **MC, hard:** Which statements about backfills are true?
  - A. **They rerun a pipeline for past periods**
  - B. **Idempotent tasks make them safe**
  - C. **They can overload the sources if run all at once**
  - D. They only process future data
- [ ] **MC, hard:** Which statements about data contracts are true?
  - A. **They define the schema and quality a producer promises**
  - B. **They catch breaking changes early**
  - C. **Consumers rely on them**
  - D. They remove the need for any tests
- [ ] **MC, hard:** Which help you observe a data pipeline?
  - A. **Freshness checks**
  - B. **Volume checks**
  - C. **Lineage between datasets**
  - D. Checking once a year
- [ ] **Blank, easy:** Bringing data from a source into your platform is called data ________.  
  Accepted: ingestion
- [ ] **Blank, easy:** Processing data in chunks at set times is ________ processing.  
  Accepted: batch
- [ ] **Blank, easy:** Processing events one by one as they arrive is ________ processing.  
  Accepted: stream, streaming
- [ ] **Blank, medium:** Scheduling and running pipeline tasks in the right order is called ________.  
  Accepted: orchestration
- [ ] **Blank, medium:** Splitting stored data into folders by a key such as date is called ________.  
  Accepted: partitioning
- [ ] **Blank, medium:** Rerunning a pipeline for past dates is called a ________.  
  Accepted: backfill
- [ ] **Blank, medium:** A check that data arrived on time measures data ________.  
  Accepted: freshness
- [ ] **Blank, hard:** A record of which datasets feed which others is called data ________.  
  Accepted: lineage
- [ ] **Blank, hard:** An agreement on the schema and quality a data producer promises is a data ________.  
  Accepted: contract
- [ ] **Blank, hard:** The architecture that uses only a streaming path, with no separate batch path, is the ________ architecture.  
  Accepted: Kappa
- [ ] **Brief, easy:** What is a data pipeline?
  - Key fact: it moves data from sources to a destination such as a warehouse
  - Key fact: on the way it cleans, transforms and validates the data
  - Key fact: it runs on a schedule or continuously and is monitored for failures
  - Keywords (grading without AI): source, destination, transform, schedule, monitor
- [ ] **Brief, medium:** When would you choose streaming over batch processing?
  - Key fact: streaming handles events as they arrive and gives results within seconds
  - Key fact: it suits fraud detection, live dashboards and alerts
  - Key fact: batch is simpler and cheaper and suits daily reports where some delay is fine
  - Keywords (grading without AI): streaming, batch, latency, real time, cost
- [ ] **Brief, medium:** How do you make a data pipeline reliable?
  - Key fact: make tasks idempotent, so retries and reruns are safe
  - Key fact: add retries, timeouts and alerts on failure
  - Key fact: check data quality and freshness at each stage
  - Keywords (grading without AI): idempotent, retry, alert, quality, freshness
- [ ] **Brief, hard:** How would you handle a change in the source schema without breaking the pipeline?
  - Key fact: detect schema changes early with validation or a schema registry
  - Key fact: accept safe changes such as new nullable columns and flag breaking ones
  - Key fact: agree on data contracts with the source team and version the schema
  - Keywords (grading without AI): schema, validation, registry, contract, version
- [ ] **Brief, hard:** How would you design a pipeline that may need to process yesterday's data again after a bug fix?
  - Key fact: keep the raw data unchanged so it can be reprocessed
  - Key fact: partition by date and make tasks idempotent, so one day can be overwritten safely
  - Key fact: run the backfill in controlled batches and check the results against expectations
  - Keywords (grading without AI): raw data, partition, idempotent, backfill, overwrite

## Apache Spark (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about Apache Spark are true?
  - A. **It processes large data in parallel across a cluster**
  - B. **It keeps data in memory where it can**
  - C. **It supports Python, Scala, Java and SQL**
  - D. It runs only on one machine
- [ ] **MC, easy:** Which of these are Spark APIs?
  - A. **DataFrame**
  - B. **Spark SQL**
  - C. **Structured Streaming**
  - D. jQuery
- [ ] **MC, easy:** Which statements about a Spark DataFrame are true?
  - A. **It is a distributed table with named columns**
  - B. **It can be queried with SQL**
  - C. **The Catalyst optimizer plans its queries**
  - D. It is stored on one computer only
- [ ] **MC, medium:** Which statements about transformations and actions are true?
  - A. **Transformations such as filter are lazy**
  - B. **Actions such as count trigger execution**
  - C. **Spark builds a plan before running it**
  - D. Transformations run immediately
- [ ] **MC, medium:** Which of these are Spark actions?
  - A. **count**
  - B. **collect**
  - C. **write**
  - D. filter
- [ ] **MC, medium:** Which statements about partitions in Spark are true?
  - A. **Data is split into partitions that are processed in parallel**
  - B. **Too few partitions leave cores idle**
  - C. **Too many tiny partitions add overhead**
  - D. The number of partitions never matters
- [ ] **MC, medium:** Which statements about caching in Spark are true?
  - A. **cache keeps a DataFrame in memory for reuse**
  - B. **It helps when the same data is used several times**
  - C. **It takes memory that other work could use**
  - D. It makes every job faster
- [ ] **MC, hard:** Which statements about shuffles are true?
  - A. **They move data between executors**
  - B. **groupBy and join can cause them**
  - C. **They are expensive because of network and disk work**
  - D. Joins never cause them
- [ ] **MC, hard:** Which statements about broadcast joins are true?
  - A. **A small table is copied to every executor**
  - B. **The large table does not need to be shuffled**
  - C. **They suit a large table joined with a small one**
  - D. They suit joining two very large tables
- [ ] **MC, hard:** Which statements about data skew are true?
  - A. **A few keys hold most of the rows**
  - B. **Some tasks take much longer than others**
  - C. **Salting keys can spread the load**
  - D. Skew makes jobs faster
- [ ] **Blank, easy:** Spark's distributed table with named columns is called a ________.  
  Accepted: DataFrame
- [ ] **Blank, easy:** The Python API for Spark is called ________.  
  Accepted: PySpark
- [ ] **Blank, easy:** The process that coordinates a Spark application is the ________.  
  Accepted: driver
- [ ] **Blank, medium:** Spark operations such as filter are ________: they run only when an action is called.  
  Accepted: lazy
- [ ] **Blank, medium:** The worker processes that run Spark tasks are called ________.  
  Accepted: executors
- [ ] **Blank, medium:** Moving data between executors for a groupBy or join is called a ________.  
  Accepted: shuffle
- [ ] **Blank, medium:** Spark SQL's query optimizer is called ________.  
  Accepted: Catalyst
- [ ] **Blank, hard:** Copying a small table to every executor to avoid a shuffle is a ________ join.  
  Accepted: broadcast
- [ ] **Blank, hard:** When a few keys hold most of the data and slow some tasks, the data is ________.  
  Accepted: skewed
- [ ] **Blank, hard:** Adding a random value to a hot key to spread it across partitions is called ________.  
  Accepted: salting
- [ ] **Brief, easy:** What is Apache Spark, and when would you use it?
  - Key fact: Spark is an engine that processes large data in parallel across a cluster
  - Key fact: it offers DataFrames, SQL, streaming and machine learning libraries
  - Key fact: use it when the data is too big or too slow for one machine
  - Keywords (grading without AI): Spark, distributed, cluster, DataFrame, parallel
- [ ] **Brief, medium:** What is lazy evaluation in Spark?
  - Key fact: transformations such as filter and select only build a plan
  - Key fact: an action such as count or write runs the plan
  - Key fact: Spark can then optimize the whole plan, for example by pushing filters down
  - Keywords (grading without AI): lazy, transformation, action, plan, optimize
- [ ] **Brief, medium:** What is the difference between narrow and wide transformations?
  - Key fact: a narrow transformation such as filter works within each partition
  - Key fact: a wide transformation such as groupBy or join needs data from many partitions
  - Key fact: wide transformations cause a shuffle, which is slower
  - Keywords (grading without AI): narrow, wide, partition, shuffle, groupBy
- [ ] **Brief, hard:** How would you speed up a slow Spark job?
  - Key fact: look in the Spark UI for long stages, shuffles and skewed tasks
  - Key fact: reduce shuffles with broadcast joins, filter early and use a sensible number of partitions
  - Key fact: fix skew with salting, and cache data that is reused
  - Keywords (grading without AI): Spark UI, shuffle, broadcast, partitions, skew
- [ ] **Brief, hard:** How would you deal with data skew in a Spark join?
  - Key fact: spot it in the Spark UI as a few tasks running much longer than the rest
  - Key fact: broadcast the smaller table if it fits in memory
  - Key fact: otherwise salt the hot keys, or turn on adaptive query execution to split skewed partitions
  - Keywords (grading without AI): skew, broadcast, salting, adaptive query execution, Spark UI

## Kafka (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about Apache Kafka are true?
  - A. **It is a distributed event streaming platform**
  - B. **Producers write records to topics**
  - C. **Consumers read records from topics**
  - D. It deletes each message as soon as it is read
- [ ] **MC, easy:** Which of these are Kafka concepts?
  - A. **Topic**
  - B. **Partition**
  - C. **Broker**
  - D. Bucket
- [ ] **MC, easy:** Which are common uses of Kafka?
  - A. **Streaming events between services**
  - B. **Collecting logs**
  - C. **Feeding real-time analytics**
  - D. Storing user passwords
- [ ] **MC, medium:** Which statements about Kafka partitions are true?
  - A. **A topic is split into partitions**
  - B. **Order is kept within a partition**
  - C. **More partitions allow more parallel consumers**
  - D. Order is kept across all partitions
- [ ] **MC, medium:** Which statements about consumer groups are true?
  - A. **Each partition is read by one consumer in the group**
  - B. **Adding consumers spreads the partitions among them**
  - C. **Different groups each read every message**
  - D. Every consumer in a group gets every message
- [ ] **MC, medium:** Which statements about offsets are true?
  - A. **An offset is a record's position in a partition**
  - B. **Consumers commit offsets to remember their progress**
  - C. **A consumer can reset its offset to replay data**
  - D. One offset is shared by all partitions
- [ ] **MC, medium:** Which statements about replication in Kafka are true?
  - A. **Each partition has a leader and followers**
  - B. **The replication factor sets how many copies exist**
  - C. **An in-sync replica can take over if the leader fails**
  - D. Replication makes Kafka lose data
- [ ] **MC, hard:** Which producer settings make writes more durable?
  - A. **acks set to all**
  - B. **Idempotence turned on**
  - C. **Retries on failure**
  - D. acks set to 0
- [ ] **MC, hard:** Which statements about log compaction are true?
  - A. **It keeps the latest value for each key**
  - B. **It suits topics that hold the current state**
  - C. **Older values for a key can be removed**
  - D. It deletes all keys after one day
- [ ] **MC, hard:** Which statements about exactly-once processing in Kafka are true?
  - A. **Idempotent producers avoid duplicate writes**
  - B. **Transactions write to several partitions atomically**
  - C. **Consumers must read only committed data to rely on it**
  - D. It is on for every client by default with no settings
- [ ] **Blank, easy:** In Kafka, records are written to named streams called ________.  
  Accepted: topics
- [ ] **Blank, easy:** A Kafka server that stores data is called a ________.  
  Accepted: broker
- [ ] **Blank, easy:** An application that writes records to Kafka is a ________.  
  Accepted: producer
- [ ] **Blank, medium:** A record's position within a partition is its ________.  
  Accepted: offset
- [ ] **Blank, medium:** Consumers that share the work of reading a topic form a consumer ________.  
  Accepted: group
- [ ] **Blank, medium:** Kafka keeps the order of records only within a single ________.  
  Accepted: partition
- [ ] **Blank, medium:** The number of copies of each partition is the replication ________.  
  Accepted: factor
- [ ] **Blank, hard:** Setting acks to ________ makes the producer wait for all in-sync replicas.  
  Accepted: all, -1
- [ ] **Blank, hard:** Log ________ keeps only the latest record for each key.  
  Accepted: compaction
- [ ] **Blank, hard:** Replicas that are fully caught up with the leader are called in-sync ________.  
  Accepted: replicas
- [ ] **Brief, easy:** What is Kafka, and what is it used for?
  - Key fact: Kafka is a distributed platform that stores streams of events in topics
  - Key fact: producers write events and consumers read them at their own pace
  - Key fact: it is used for event-driven services, log collection and real-time analytics
  - Keywords (grading without AI): Kafka, topic, producer, consumer, stream
- [ ] **Brief, medium:** How do partitions and consumer groups let Kafka scale?
  - Key fact: a topic is split into partitions spread across brokers
  - Key fact: in a consumer group, each partition is read by one consumer, so the work is shared
  - Key fact: more partitions allow more parallel consumers, and order is kept within each partition
  - Keywords (grading without AI): partition, consumer group, parallel, order, broker
- [ ] **Brief, medium:** How does Kafka differ from a traditional message queue?
  - Key fact: Kafka keeps messages for a set time instead of deleting them once read
  - Key fact: consumers track their own offsets and can replay old data
  - Key fact: it is built for very high throughput and many independent consumers of the same data
  - Keywords (grading without AI): retention, offset, replay, throughput, log
- [ ] **Brief, hard:** How do you avoid losing or duplicating messages with Kafka?
  - Key fact: on the producer, use acks set to all, retries and idempotence
  - Key fact: replicate partitions, for example with a replication factor of 3 and a minimum number of in-sync replicas
  - Key fact: on the consumer, commit offsets after processing and make processing idempotent, or use transactions
  - Keywords (grading without AI): acks, idempotent, replication, offset commit, transactions
- [ ] **Brief, hard:** How would you choose the number of partitions for a topic?
  - Key fact: partitions set the maximum number of consumers in a group that can work in parallel
  - Key fact: estimate the throughput needed and what one consumer can handle
  - Key fact: more partitions add overhead and changing the count later disturbs key order, so plan for growth without going overboard
  - Keywords (grading without AI): partitions, throughput, parallelism, consumer, overhead

## Data Warehousing (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about a data warehouse are true?
  - A. **It stores cleaned data for analysis**
  - B. **It combines data from many sources**
  - C. **It is optimized for reading and aggregating**
  - D. It is the main database for a live app's transactions
- [ ] **MC, easy:** Which of these are cloud data warehouses?
  - A. **Snowflake**
  - B. **Google BigQuery**
  - C. **Amazon Redshift**
  - D. Microsoft Word
- [ ] **MC, easy:** Which statements about OLTP and OLAP are true?
  - A. **OLTP handles many small transactions**
  - B. **OLAP handles analytical queries over large data**
  - C. **A data warehouse is an OLAP system**
  - D. They are the same thing
- [ ] **MC, medium:** Which statements about a star schema are true?
  - A. **A central fact table holds measurements**
  - B. **Dimension tables hold descriptive attributes**
  - C. **Dimensions join to the fact table by keys**
  - D. It has no fact table
- [ ] **MC, medium:** Which statements about columnar storage are true?
  - A. **It stores each column's values together**
  - B. **Queries that read few columns run faster**
  - C. **It compresses well**
  - D. It is best for updating single rows often
- [ ] **MC, medium:** Which statements about a data lake are true?
  - A. **It stores raw data in many formats**
  - B. **It often lives on object storage such as S3**
  - C. **Structure is applied when the data is read**
  - D. It stores only cleaned tables
- [ ] **MC, medium:** Which statements about the grain of a fact table are true?
  - A. **It is what one row represents**
  - B. **It should be decided first**
  - C. **Mixing grains in one table gives wrong totals**
  - D. It does not matter
- [ ] **MC, hard:** Which statements about a lakehouse are true?
  - A. **It adds warehouse features to a data lake**
  - B. **Table formats such as Delta Lake or Iceberg add transactions**
  - C. **It keeps data in open file formats**
  - D. It cannot run SQL
- [ ] **MC, hard:** Which make warehouse queries faster and cheaper?
  - A. **Partitioning or clustering large tables**
  - B. **Selecting only the needed columns**
  - C. **Materialized views for common aggregates**
  - D. SELECT * in every query
- [ ] **MC, hard:** Which statements about conformed dimensions are true?
  - A. **Several fact tables share them**
  - B. **They let you compare metrics across business processes**
  - C. **A shared date dimension is an example**
  - D. Each fact table needs its own copy with a different meaning
- [ ] **Blank, easy:** Systems built for analytical queries over large data are called ________ systems.  
  Accepted: OLAP
- [ ] **Blank, easy:** Systems built for many small transactions are called ________ systems.  
  Accepted: OLTP
- [ ] **Blank, easy:** A store of raw data in many formats, often on object storage, is a data ________.  
  Accepted: lake
- [ ] **Blank, medium:** In a star schema, the table that holds the measurements is the ________ table.  
  Accepted: fact
- [ ] **Blank, medium:** Storing data column by column is called ________ storage.  
  Accepted: columnar, column-oriented
- [ ] **Blank, medium:** What one row of a fact table represents is called its ________.  
  Accepted: grain
- [ ] **Blank, medium:** A schema whose dimensions are split into further normalized tables is a ________ schema.  
  Accepted: snowflake
- [ ] **Blank, hard:** A stored query result that is refreshed for speed is a ________ view.  
  Accepted: materialized
- [ ] **Blank, hard:** A dimension shared by several fact tables with the same meaning is a ________ dimension.  
  Accepted: conformed
- [ ] **Blank, hard:** An architecture that adds transactions and schemas to a data lake is called a ________.  
  Accepted: lakehouse
- [ ] **Brief, easy:** What is a data warehouse, and how is it different from an application database?
  - Key fact: a warehouse stores cleaned, historical data from many sources for analysis
  - Key fact: an application database handles many small reads and writes for a live app
  - Key fact: warehouses are optimized for large scans and aggregations, often with columnar storage
  - Keywords (grading without AI): warehouse, OLAP, OLTP, analysis, columnar
- [ ] **Brief, medium:** Explain the star schema.
  - Key fact: a central fact table holds measurements such as sales amounts
  - Key fact: dimension tables such as date, product and customer describe the facts
  - Key fact: queries join facts to dimensions to filter and group, which is simple and fast
  - Keywords (grading without AI): star schema, fact, dimension, join, grain
- [ ] **Brief, medium:** What is the difference between a data lake and a data warehouse?
  - Key fact: a data lake stores raw data of any format cheaply, often on object storage
  - Key fact: a warehouse stores structured, cleaned data ready for SQL analysis
  - Key fact: many teams land raw data in a lake and load curated tables into a warehouse or lakehouse
  - Keywords (grading without AI): data lake, warehouse, raw, structured, lakehouse
- [ ] **Brief, hard:** How would you design a fact table for online orders?
  - Key fact: choose the grain first, for example one row per order line
  - Key fact: add foreign keys to dimensions such as date, customer and product
  - Key fact: store additive measures such as quantity and amount, and keep the grain consistent
  - Keywords (grading without AI): grain, fact table, dimension, measure, additive
- [ ] **Brief, hard:** How would you make a slow, expensive warehouse query cheaper?
  - Key fact: select only the needed columns and filter on partitioned or clustered columns
  - Key fact: pre-aggregate common results in materialized views or summary tables
  - Key fact: read the query plan for large scans and joins, and avoid repeated full refreshes
  - Keywords (grading without AI): columns, partition, materialized view, query plan, cost

## Airflow (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about Apache Airflow are true?
  - A. **It schedules and monitors workflows**
  - B. **Workflows are written in Python**
  - C. **It shows task status in a web UI**
  - D. It is a database
- [ ] **MC, easy:** Which of these are Airflow concepts?
  - A. **DAG**
  - B. **Task**
  - C. **Operator**
  - D. Bucket
- [ ] **MC, easy:** Which statements about an Airflow DAG are true?
  - A. **It is a set of tasks with dependencies**
  - B. **It has no cycles**
  - C. **It has a schedule**
  - D. A task in it can depend on itself
- [ ] **MC, medium:** Which statements about operators are true?
  - A. **An operator defines what a task does**
  - B. **PythonOperator runs a Python function**
  - C. **BashOperator runs a shell command**
  - D. Operators store large datasets
- [ ] **MC, medium:** Which statements about task dependencies are true?
  - A. **a >> b means b runs after a**
  - B. **Dependencies decide the order of tasks**
  - C. **Tasks that do not depend on each other can run in parallel**
  - D. Dependencies are set by hand in the database
- [ ] **MC, medium:** Which statements about retries in Airflow are true?
  - A. **retries sets how many times a task is retried**
  - B. **retry_delay sets the wait between tries**
  - C. **Retries help with temporary failures**
  - D. Retries fix bugs in the code
- [ ] **MC, medium:** Which statements about XComs are true?
  - A. **They pass small values between tasks**
  - B. **They are stored in the metadata database**
  - C. **They are not meant for large data**
  - D. They are the best way to move gigabytes
- [ ] **MC, hard:** Which statements about catchup and backfill are true?
  - A. **catchup runs the intervals missed since the start date**
  - B. **With catchup off, only the latest interval runs**
  - C. **A backfill runs a DAG for a chosen past range**
  - D. catchup deletes old runs
- [ ] **MC, hard:** Which make Airflow tasks safer?
  - A. **Making tasks idempotent**
  - B. **Using the run's logical date instead of the current time**
  - C. **Keeping heavy processing out of the scheduler**
  - D. Doing heavy work at the top level of the DAG file
- [ ] **MC, hard:** Which statements about sensors are true?
  - A. **They wait for a condition such as a file arriving**
  - B. **Reschedule mode frees the worker slot while waiting**
  - C. **Deferrable operators wait without holding a worker**
  - D. A sensor checks only once and never again
- [ ] **Blank, easy:** An Airflow workflow is defined as a ________.  
  Accepted: DAG, directed acyclic graph
- [ ] **Blank, easy:** Airflow workflows are written in the ________ language.  
  Accepted: Python
- [ ] **Blank, easy:** A single unit of work in a DAG is a ________.  
  Accepted: task
- [ ] **Blank, medium:** The class that defines what a task does, such as PythonOperator, is an ________.  
  Accepted: operator
- [ ] **Blank, medium:** In Airflow, a >> b means task b runs ________ task a.  
  Accepted: after
- [ ] **Blank, medium:** Small values passed between Airflow tasks are called ________.  
  Accepted: XComs, XCom
- [ ] **Blank, medium:** A task that waits for a condition, such as a file appearing, is a ________.  
  Accepted: sensor
- [ ] **Blank, hard:** Whether a DAG runs the past intervals it missed is controlled by the ________ setting.  
  Accepted: catchup
- [ ] **Blank, hard:** The date a DAG run is for, which should be used instead of the current time, is the logical ________.  
  Accepted: date
- [ ] **Blank, hard:** Operators that wait without holding a worker slot are called ________ operators.  
  Accepted: deferrable
- [ ] **Brief, easy:** What is Apache Airflow used for?
  - Key fact: it schedules, runs and monitors data workflows
  - Key fact: workflows are DAGs of tasks with dependencies, written in Python
  - Key fact: its web UI shows runs, logs and failures, and it retries failed tasks
  - Keywords (grading without AI): Airflow, DAG, schedule, task, monitor
- [ ] **Brief, medium:** What is a DAG in Airflow, and why must it have no cycles?
  - Key fact: a DAG is a set of tasks connected by dependencies that decide the order
  - Key fact: acyclic means no task can depend on itself through a loop
  - Key fact: without cycles Airflow can always work out which tasks may run next
  - Keywords (grading without AI): DAG, dependency, acyclic, order, task
- [ ] **Brief, medium:** How do you pass data between Airflow tasks?
  - Key fact: XComs pass small values such as file paths or IDs
  - Key fact: large data should be written to storage such as S3 or a table
  - Key fact: the next task reads the data from the path passed through XCom
  - Keywords (grading without AI): XCom, storage, path, small values, S3
- [ ] **Brief, hard:** How do you make Airflow tasks safe to rerun and backfill?
  - Key fact: make each task idempotent, for example by overwriting the partition for its date
  - Key fact: use the run's logical date instead of the current time
  - Key fact: keep tasks small and independent, so a single failed task can be retried
  - Keywords (grading without AI): idempotent, logical date, backfill, partition, retry
- [ ] **Brief, hard:** What are common mistakes when writing Airflow DAGs?
  - Key fact: heavy work or database calls at the top level of the DAG file, which runs on every parse
  - Key fact: passing large data through XCom
  - Key fact: using the current time instead of the logical date, which breaks backfills
  - Keywords (grading without AI): top-level code, XCom, logical date, parse, backfill

## Cloud Storage (bank_dataeng.py)

- [ ] **MC, easy:** Which of these are object storage services?
  - A. **Amazon S3**
  - B. **Google Cloud Storage**
  - C. **Azure Blob Storage**
  - D. Amazon RDS
- [ ] **MC, easy:** Which statements about object storage are true?
  - A. **Data is stored as objects in buckets**
  - B. **Each object has a key**
  - C. **It scales to very large amounts of data**
  - D. By default it works like a disk mounted with a file system
- [ ] **MC, easy:** Which are common uses of object storage?
  - A. **Backups**
  - B. **Data lake files**
  - C. **Files of a static website**
  - D. Running a transactional database
- [ ] **MC, medium:** Which statements about storage classes are true?
  - A. **Infrequent-access classes cost less to store but more to read**
  - B. **Archive classes such as Glacier are cheapest for long-term storage**
  - C. **Lifecycle rules can move objects between classes**
  - D. Every class has the same price
- [ ] **MC, medium:** Which statements about bucket security are true?
  - A. **Buckets should be private by default**
  - B. **Access is controlled with policies and roles**
  - C. **Pre-signed URLs give temporary access**
  - D. Public read access is the safest default
- [ ] **MC, medium:** Which statements about object versioning are true?
  - A. **It keeps old versions of an object**
  - B. **It protects against accidental overwrite or delete**
  - C. **It increases storage use**
  - D. Every bucket must have it
- [ ] **MC, medium:** Which file formats suit analytics in a data lake?
  - A. **Parquet**
  - B. **ORC**
  - C. **Avro**
  - D. Word documents
- [ ] **MC, hard:** Which statements about Parquet are true?
  - A. **It is a columnar format**
  - B. **It compresses well**
  - C. **Query engines can skip columns and row groups**
  - D. It is a row-based text format
- [ ] **MC, hard:** Which reduce cloud storage costs?
  - A. **Lifecycle rules that archive old data**
  - B. **Compressing files**
  - C. **Deleting copies nobody needs**
  - D. Keeping everything in the most expensive class
- [ ] **MC, hard:** Which statements about encryption in cloud storage are true?
  - A. **The provider can encrypt data at rest**
  - B. **Customer-managed keys give more control**
  - C. **Data should be encrypted in transit with TLS**
  - D. Encryption makes access control unnecessary
- [ ] **Blank, easy:** In Amazon S3, objects are stored in containers called ________.  
  Accepted: buckets
- [ ] **Blank, easy:** Amazon's object storage service is called ________.  
  Accepted: S3, Simple Storage Service
- [ ] **Blank, easy:** Each object in a bucket is found by its ________.  
  Accepted: key
- [ ] **Blank, medium:** Rules that move or delete objects after a set time are ________ rules.  
  Accepted: lifecycle
- [ ] **Blank, medium:** A ________ URL gives temporary access to a private object.  
  Accepted: pre-signed, presigned, signed
- [ ] **Blank, medium:** Keeping older copies of objects when they change is called ________.  
  Accepted: versioning
- [ ] **Blank, medium:** Encrypting data while it is stored is called encryption at ________.  
  Accepted: rest
- [ ] **Blank, hard:** ________ is a columnar file format widely used in data lakes.  
  Accepted: Parquet
- [ ] **Blank, hard:** Amazon's low-cost storage class for long-term archives is S3 ________.  
  Accepted: Glacier
- [ ] **Blank, hard:** Folders in a data lake named like date=2024-01-01 follow ________-style partitioning.  
  Accepted: Hive
- [ ] **Brief, easy:** What is object storage, and when would you use it?
  - Key fact: object storage keeps files as objects with keys in buckets, as in S3
  - Key fact: it is cheap, durable and scales to huge amounts of data
  - Key fact: use it for backups, media, static files and data lake storage
  - Keywords (grading without AI): object storage, bucket, S3, durable, data lake
- [ ] **Brief, medium:** How do you keep data in a cloud bucket secure?
  - Key fact: keep buckets private and block public access by default
  - Key fact: grant least-privilege access with IAM roles and bucket policies, and use pre-signed URLs for temporary access
  - Key fact: encrypt data at rest and in transit, and log access
  - Keywords (grading without AI): private, IAM, least privilege, encryption, pre-signed URL
- [ ] **Brief, medium:** How do storage classes and lifecycle rules save money?
  - Key fact: frequently used data stays in the standard class
  - Key fact: rarely used data moves to infrequent-access or archive classes that cost less to store
  - Key fact: lifecycle rules move or delete objects automatically after a set age
  - Keywords (grading without AI): storage class, lifecycle, archive, infrequent access, cost
- [ ] **Brief, hard:** Why are columnar formats such as Parquet used in data lakes?
  - Key fact: they store each column together, so queries read only the columns they need
  - Key fact: they compress well because the values in a column are similar
  - Key fact: they keep statistics that let engines skip whole row groups, which cuts cost and time
  - Keywords (grading without AI): Parquet, columnar, compression, row group, statistics
- [ ] **Brief, hard:** How would you organize files in a data lake on object storage?
  - Key fact: separate zones such as raw, cleaned and curated
  - Key fact: partition folders by the fields queries filter on, such as date
  - Key fact: use a columnar format, avoid many tiny files and register the tables in a catalog
  - Keywords (grading without AI): zones, partition, Parquet, small files, catalog

## Data Modeling (bank_dataeng.py)

- [ ] **MC, easy:** Which are levels of data modeling?
  - A. **Conceptual**
  - B. **Logical**
  - C. **Physical**
  - D. Visual
- [ ] **MC, easy:** Which statements about entities and relationships are true?
  - A. **An entity is a thing you store data about**
  - B. **A relationship links entities**
  - C. **One-to-many is a kind of relationship**
  - D. Entities have no attributes
- [ ] **MC, easy:** Which of these are kinds of relationship?
  - A. **One-to-one**
  - B. **One-to-many**
  - C. **Many-to-many**
  - D. None-to-all
- [ ] **MC, medium:** Which statements about normalization are true?
  - A. **It removes duplicated data**
  - B. **It splits data into related tables**
  - C. **It reduces update anomalies**
  - D. It always makes reads faster
- [ ] **MC, medium:** Which statements about a many-to-many relationship are true?
  - A. **A relational database needs a junction table for it**
  - B. **The junction table holds foreign keys to both tables**
  - C. **Students and courses are an example**
  - D. One foreign key column is enough to store it
- [ ] **MC, medium:** Which statements about denormalization are true?
  - A. **It adds some duplication to speed up reads**
  - B. **It is common in warehouses**
  - C. **It makes updates more complex**
  - D. It removes all duplicates
- [ ] **MC, medium:** Which statements about primary and foreign keys are true?
  - A. **A primary key identifies each row**
  - B. **A foreign key refers to a primary key in another table**
  - C. **Foreign keys keep references valid**
  - D. A foreign key column must hold unique values
- [ ] **MC, hard:** Which statements about normal forms are true?
  - A. **1NF needs atomic values**
  - B. **2NF removes partial dependencies on a composite key**
  - C. **3NF removes transitive dependencies**
  - D. 3NF allows repeating groups
- [ ] **MC, hard:** Which statements about dimensional modeling are true?
  - A. **It organizes data into facts and dimensions**
  - B. **Kimball's approach builds data marts around business processes**
  - C. **It favours simple queries over full normalization**
  - D. It forbids dimension tables
- [ ] **MC, hard:** Which statements about Data Vault modeling are true?
  - A. **Hubs hold business keys**
  - B. **Links hold relationships**
  - C. **Satellites hold descriptive attributes and their history**
  - D. It uses a single table
- [ ] **Blank, easy:** A diagram of entities and their relationships is an ________ diagram.  
  Accepted: ER, entity-relationship, entity relationship
- [ ] **Blank, easy:** A thing you store data about, such as a customer, is an ________.  
  Accepted: entity
- [ ] **Blank, easy:** A column that uniquely identifies each row is a ________ key.  
  Accepted: primary
- [ ] **Blank, medium:** Organizing tables to remove duplicated data is called ________.  
  Accepted: normalization, normalisation
- [ ] **Blank, medium:** A table that links two tables in a many-to-many relationship is a ________ table.  
  Accepted: junction, bridge, join, associative
- [ ] **Blank, medium:** Adding duplication on purpose to speed up reads is called ________.  
  Accepted: denormalization, denormalisation
- [ ] **Blank, medium:** The model that describes tables, columns and types for a specific database is the ________ model.  
  Accepted: physical
- [ ] **Blank, hard:** Third normal form removes ________ dependencies.  
  Accepted: transitive
- [ ] **Blank, hard:** In Data Vault, a table that holds unique business keys is a ________.  
  Accepted: hub
- [ ] **Blank, hard:** A key made of two or more columns is a ________ key.  
  Accepted: composite, compound
- [ ] **Brief, easy:** What is data modeling, and why does it matter?
  - Key fact: it defines what data is stored, how it is structured and how the pieces relate
  - Key fact: it moves from a conceptual to a logical to a physical model
  - Key fact: a good model keeps data consistent and makes queries and changes easier
  - Keywords (grading without AI): entity, relationship, conceptual, logical, physical
- [ ] **Brief, medium:** What is normalization, and when would you denormalize?
  - Key fact: normalization splits data into related tables to remove duplication and update anomalies
  - Key fact: it suits transactional systems with many writes
  - Key fact: denormalize for read-heavy analytics, accepting some duplication for simpler, faster queries
  - Keywords (grading without AI): normalization, denormalization, duplication, anomaly, read-heavy
- [ ] **Brief, medium:** How do you model a many-to-many relationship?
  - Key fact: create a junction table between the two tables
  - Key fact: it holds foreign keys to both, often as a composite primary key
  - Key fact: it can also hold attributes of the relationship, such as the date a student enrolled
  - Keywords (grading without AI): many-to-many, junction table, foreign key, composite key, attribute
- [ ] **Brief, hard:** Explain the first three normal forms.
  - Key fact: 1NF: each column holds one atomic value and there are no repeating groups
  - Key fact: 2NF: every non-key column depends on the whole primary key, not on part of it
  - Key fact: 3NF: non-key columns depend only on the key, not on other non-key columns
  - Keywords (grading without AI): 1NF, 2NF, 3NF, atomic, dependency
- [ ] **Brief, hard:** How would you model the data for an online shop's orders?
  - Key fact: entities such as customer, product, order and order line, with order lines linking orders and products
  - Key fact: keys and foreign keys make each order line point to one order and one product
  - Key fact: store the price paid on the order line, because product prices change over time
  - Keywords (grading without AI): order, order line, foreign key, product, price history

## CI/CD (bank_devops.py)

- [ ] **MC, easy:** Which statements about CI/CD are true?
  - A. **CI builds and tests every change automatically**
  - B. **CD delivers tested changes to environments automatically**
  - C. **It catches problems early**
  - D. It means deploying without any tests
- [ ] **MC, easy:** Which of these are CI/CD tools?
  - A. **GitHub Actions**
  - B. **Jenkins**
  - C. **GitLab CI**
  - D. Microsoft Paint
- [ ] **MC, easy:** Which steps often appear in a CI pipeline?
  - A. **Checking out the code**
  - B. **Building**
  - C. **Running the tests**
  - D. Deleting the repository
- [ ] **MC, medium:** Which statements about continuous delivery and continuous deployment are true?
  - A. **Continuous delivery keeps every change ready to release behind a manual approval**
  - B. **Continuous deployment releases every passing change automatically**
  - C. **Both need strong automated tests**
  - D. They are the same thing
- [ ] **MC, medium:** Which make a pipeline faster?
  - A. **Caching dependencies**
  - B. **Running jobs in parallel**
  - C. **Running only the tests a change affects, where that is safe**
  - D. Rebuilding everything from scratch each time with no cache
- [ ] **MC, medium:** Which statements about secrets in a pipeline are true?
  - A. **Keep them in the CI tool's secret store**
  - B. **Mask them in the logs**
  - C. **Give each pipeline only the secrets it needs**
  - D. Commit them to the repository
- [ ] **MC, medium:** Which statements about build artifacts are true?
  - A. **Build once and deploy the same artifact to every environment**
  - B. **Store artifacts in a registry with a version**
  - C. **The artifact should not be rebuilt for each environment**
  - D. Each environment should build its own copy from source
- [ ] **MC, hard:** Which deployment strategies reduce risk?
  - A. **Blue-green deployment**
  - B. **Canary release**
  - C. **Rolling update**
  - D. Replacing every server at once with no rollback
- [ ] **MC, hard:** Which statements about feature flags are true?
  - A. **They turn features on or off without a deploy**
  - B. **They support trunk-based development**
  - C. **Old flags should be removed**
  - D. They replace testing
- [ ] **MC, hard:** Which statements about trunk-based development are true?
  - A. **Developers merge small changes into main often**
  - B. **It needs fast CI and good tests**
  - C. **Feature flags hide unfinished work**
  - D. Branches live for months
- [ ] **Blank, easy:** CI stands for continuous ________.  
  Accepted: integration
- [ ] **Blank, easy:** A series of automated steps that build, test and deploy code is a ________.  
  Accepted: pipeline
- [ ] **Blank, easy:** GitHub's built-in CI/CD service is GitHub ________.  
  Accepted: Actions
- [ ] **Blank, medium:** The file a build produces, such as a JAR or a Docker image, is a build ________.  
  Accepted: artifact
- [ ] **Blank, medium:** Releasing every change that passes the pipeline automatically is continuous ________.  
  Accepted: deployment
- [ ] **Blank, medium:** Keeping two identical environments and switching traffic between them is ________ deployment.  
  Accepted: blue-green, blue green
- [ ] **Blank, medium:** A switch that turns a feature on or off without a deploy is a feature ________.  
  Accepted: flag, toggle
- [ ] **Blank, hard:** Releasing to a small share of users first is a ________ release.  
  Accepted: canary
- [ ] **Blank, hard:** Merging small changes into main many times a day is ________-based development.  
  Accepted: trunk
- [ ] **Blank, hard:** Going back to the previous version after a bad release is a ________.  
  Accepted: rollback
- [ ] **Brief, easy:** What are continuous integration and continuous delivery?
  - Key fact: continuous integration builds and tests every change automatically when it is pushed
  - Key fact: continuous delivery keeps the code always ready to release through automated steps
  - Key fact: together they catch problems early and make releases small and routine
  - Keywords (grading without AI): continuous integration, continuous delivery, build, test, release
- [ ] **Brief, medium:** What would a good CI pipeline for a web app include?
  - Key fact: check out the code, install dependencies with caching and build
  - Key fact: run linting, unit tests and integration tests
  - Key fact: build a versioned artifact or image and publish it to a registry
  - Keywords (grading without AI): build, test, lint, artifact, cache
- [ ] **Brief, medium:** How do you handle secrets in a CI/CD pipeline?
  - Key fact: keep them in the CI tool's secret store or a vault, never in the repository
  - Key fact: mask them in logs and give each job only the secrets it needs
  - Key fact: prefer short-lived credentials, such as OIDC federation to the cloud
  - Keywords (grading without AI): secret store, vault, mask, least privilege, OIDC
- [ ] **Brief, hard:** Compare blue-green and canary deployments.
  - Key fact: blue-green runs two environments and switches all traffic to the new one at once, with an instant switch back
  - Key fact: a canary sends a small share of traffic to the new version and grows it while watching metrics
  - Key fact: a canary limits the impact further, while blue-green gives a simpler rollback
  - Keywords (grading without AI): blue-green, canary, traffic, rollback, metrics
- [ ] **Brief, hard:** How would you make deployments safe enough to release many times a day?
  - Key fact: keep changes small and covered by fast automated tests
  - Key fact: use feature flags to separate deploying code from releasing features
  - Key fact: deploy gradually with health checks and roll back automatically when metrics get worse
  - Keywords (grading without AI): small changes, tests, feature flags, health checks, rollback

## Kubernetes (bank_devops.py)

- [ ] **MC, easy:** Which statements about Kubernetes are true?
  - A. **It orchestrates containers across machines**
  - B. **It restarts failed containers**
  - C. **It scales applications**
  - D. It is a programming language
- [ ] **MC, easy:** Which of these are Kubernetes objects?
  - A. **Pod**
  - B. **Deployment**
  - C. **Service**
  - D. Dockerfile
- [ ] **MC, easy:** Which statements about a Pod are true?
  - A. **It is the smallest deployable unit**
  - B. **It can hold one or more containers**
  - C. **Containers in a Pod share its network**
  - D. A Pod lives forever once it is created
- [ ] **MC, medium:** Which statements about a Deployment are true?
  - A. **It keeps a set number of Pod replicas running**
  - B. **It performs rolling updates**
  - C. **It can roll back to an earlier version**
  - D. It exposes Pods to the internet by itself
- [ ] **MC, medium:** Which statements about Services are true?
  - A. **They give Pods a stable address**
  - B. **ClusterIP is reachable only inside the cluster**
  - C. **LoadBalancer exposes a Service externally through the cloud**
  - D. They store configuration files
- [ ] **MC, medium:** Which statements about ConfigMaps and Secrets are true?
  - A. **ConfigMaps hold configuration**
  - B. **Secrets hold sensitive values**
  - C. **Both can be mounted as files or passed as environment variables**
  - D. Secrets are strongly encrypted by default everywhere
- [ ] **MC, medium:** Which statements about probes are true?
  - A. **A liveness probe restarts a stuck container**
  - B. **A readiness probe keeps traffic away until the Pod is ready**
  - C. **A startup probe gives slow apps time to start**
  - D. Probes are only for logging
- [ ] **MC, hard:** Which statements about resource requests and limits are true?
  - A. **The scheduler uses requests to place Pods**
  - B. **A container over its memory limit is killed**
  - C. **A container over its CPU limit is throttled**
  - D. Kubernetes ignores limits
- [ ] **MC, hard:** Which statements about the Horizontal Pod Autoscaler are true?
  - A. **It changes the number of replicas based on metrics**
  - B. **CPU use is a common metric**
  - C. **It needs resource requests to compute utilisation**
  - D. It adds memory to a single Pod
- [ ] **MC, hard:** Which statements about StatefulSets are true?
  - A. **They give Pods stable names and storage**
  - B. **They suit databases**
  - C. **Their Pods start in order**
  - D. They are the same as Deployments
- [ ] **Blank, easy:** The smallest deployable unit in Kubernetes is a ________.  
  Accepted: Pod
- [ ] **Blank, easy:** The command-line tool for Kubernetes is ________.  
  Accepted: kubectl
- [ ] **Blank, easy:** Kubernetes objects are usually described in ________ files.  
  Accepted: YAML
- [ ] **Blank, medium:** A ________ keeps a set number of identical Pods running and rolls out updates.  
  Accepted: Deployment
- [ ] **Blank, medium:** A ________ gives a set of Pods a stable network address.  
  Accepted: Service
- [ ] **Blank, medium:** A ________ probe tells Kubernetes when a Pod can receive traffic.  
  Accepted: readiness
- [ ] **Blank, medium:** Sensitive values such as passwords are kept in a Kubernetes ________.  
  Accepted: Secret
- [ ] **Blank, hard:** The Horizontal Pod ________ changes the number of replicas based on metrics.  
  Accepted: Autoscaler
- [ ] **Blank, hard:** A ________ gives each Pod a stable name and its own storage, which suits databases.  
  Accepted: StatefulSet
- [ ] **Blank, hard:** An ________ routes external HTTP traffic to Services by host and path.  
  Accepted: Ingress
- [ ] **Brief, easy:** What is Kubernetes, and why use it?
  - Key fact: it runs and manages containers across a cluster of machines
  - Key fact: it restarts failed containers, scales apps and rolls out updates
  - Key fact: you describe the desired state in YAML, and Kubernetes keeps the cluster in that state
  - Keywords (grading without AI): containers, cluster, scale, desired state, self-healing
- [ ] **Brief, medium:** What is the difference between a Pod, a Deployment and a Service?
  - Key fact: a Pod runs one or more containers and can be replaced at any time
  - Key fact: a Deployment keeps the right number of Pods running and handles rolling updates
  - Key fact: a Service gives those Pods one stable address and spreads traffic across them
  - Keywords (grading without AI): Pod, Deployment, Service, replica, stable address
- [ ] **Brief, medium:** What are liveness and readiness probes for?
  - Key fact: a liveness probe restarts a container that is stuck
  - Key fact: a readiness probe keeps traffic away until the Pod is ready
  - Key fact: without them, users can reach Pods that are still starting or are broken
  - Keywords (grading without AI): liveness, readiness, restart, traffic, health
- [ ] **Brief, hard:** How do resource requests and limits work in Kubernetes?
  - Key fact: requests reserve CPU and memory and guide where the scheduler places a Pod
  - Key fact: limits cap usage: CPU is throttled, and a container over its memory limit is killed
  - Key fact: setting them well avoids noisy neighbours and lets autoscaling work
  - Keywords (grading without AI): requests, limits, scheduler, OOMKilled, throttling
- [ ] **Brief, hard:** A Pod keeps restarting. How would you debug it?
  - Key fact: run kubectl describe pod to see the events and the last state, such as OOMKilled or a failing probe
  - Key fact: read the logs of the previous container with kubectl logs --previous
  - Key fact: check the image, the configuration, the resource limits and the probe settings, then fix the cause
  - Keywords (grading without AI): kubectl describe, logs, CrashLoopBackOff, OOMKilled, probe

## Linux (bank_devops.py)

- [ ] **MC, easy:** Which commands show a directory listing or file contents?
  - A. **ls**
  - B. **cat**
  - C. chmod
  - D. **head**
- [ ] **MC, easy:** Which statements about Linux file permissions are true?
  - A. **Permissions are set for the owner, the group and others**
  - B. **r, w and x mean read, write and execute**
  - C. **chmod 755 gives the owner full rights**
  - D. Every user can edit every file
- [ ] **MC, easy:** Which commands work with files and folders?
  - A. **cp**
  - B. **mv**
  - C. **rm**
  - D. ping
- [ ] **MC, medium:** Which commands help you find things?
  - A. **grep searches for text in files**
  - B. **find searches for files by name and other tests**
  - C. **which shows where a command lives**
  - D. cd searches the whole disk
- [ ] **MC, medium:** Which commands show what is running and what resources are used?
  - A. **ps**
  - B. **top**
  - C. **free**
  - D. touch
- [ ] **MC, medium:** Which statements about processes and signals are true?
  - A. **kill sends a signal to a process**
  - B. **SIGTERM asks a process to stop cleanly**
  - C. **SIGKILL cannot be caught**
  - D. Every process has the same ID
- [ ] **MC, medium:** Which statements about systemd are true?
  - A. **systemctl starts, stops and restarts services**
  - B. **systemctl enable starts a service at boot**
  - C. **journalctl shows service logs**
  - D. It is a text editor
- [ ] **MC, hard:** Which statements about standard streams are true?
  - A. **stdout is file descriptor 1**
  - B. **stderr is file descriptor 2**
  - C. **2>&1 sends stderr to the same place as stdout**
  - D. A pipe passes stderr to the next command by default
- [ ] **MC, hard:** Which help when a disk is full?
  - A. **df -h shows free space for each filesystem**
  - B. **du -sh shows the size of folders**
  - C. **Deleting or rotating old logs**
  - D. Running chmod on every file
- [ ] **MC, hard:** Which statements about users and sudo are true?
  - A. **root has full control of the system**
  - B. **sudo runs a command with elevated rights**
  - C. **Working as root all the time is risky**
  - D. sudo turns off all logging
- [ ] **Blank, easy:** The ________ command changes the permissions of a file.  
  Accepted: chmod
- [ ] **Blank, easy:** The ________ command prints the current working directory.  
  Accepted: pwd
- [ ] **Blank, easy:** The ________ command lists the files in a directory.  
  Accepted: ls
- [ ] **Blank, medium:** The ________ command searches for text patterns inside files.  
  Accepted: grep
- [ ] **Blank, medium:** The ________ command shows running processes and live resource use.  
  Accepted: top, htop
- [ ] **Blank, medium:** The command ________ -h shows the free disk space on each filesystem.  
  Accepted: df
- [ ] **Blank, medium:** The ________ command changes the owner of a file.  
  Accepted: chown
- [ ] **Blank, hard:** The signal sent by kill -9, which cannot be caught, is ________.  
  Accepted: SIGKILL
- [ ] **Blank, hard:** The command ________ shows the logs collected by systemd.  
  Accepted: journalctl
- [ ] **Blank, hard:** In a shell, 2>&1 redirects standard ________ to standard output.  
  Accepted: error
- [ ] **Brief, easy:** Explain Linux file permissions.
  - Key fact: each file has permissions for its owner, its group and others
  - Key fact: r means read, w write and x execute, also written as numbers such as 755
  - Key fact: chmod changes the permissions and chown changes the owner
  - Keywords (grading without AI): owner, group, read, chmod, 755
- [ ] **Brief, medium:** How would you find which process uses the most CPU or memory?
  - Key fact: run top or htop and sort by CPU or memory
  - Key fact: list the heaviest processes with ps aux and sort
  - Key fact: look into the process by its ID, and stop it with kill if needed
  - Keywords (grading without AI): top, htop, ps, PID, kill
- [ ] **Brief, medium:** How do you manage a service on a Linux server with systemd?
  - Key fact: systemctl start, stop and restart control the service
  - Key fact: systemctl enable makes it start at boot, and status shows whether it runs
  - Key fact: journalctl -u shows its logs
  - Keywords (grading without AI): systemctl, enable, status, journalctl, service
- [ ] **Brief, hard:** A server's disk is full. How would you find and fix the cause?
  - Key fact: check the usage of each filesystem with df -h
  - Key fact: find the largest folders with du and sort them, often logs or temporary files
  - Key fact: remove or compress old files and set up log rotation so it does not happen again
  - Keywords (grading without AI): df, du, logs, logrotate, cleanup
- [ ] **Brief, hard:** What is the difference between SIGTERM and SIGKILL?
  - Key fact: SIGTERM asks a process to stop and lets it clean up
  - Key fact: SIGKILL stops it at once and cannot be caught or ignored
  - Key fact: send SIGTERM first, and use SIGKILL only if the process does not stop
  - Keywords (grading without AI): SIGTERM, SIGKILL, signal, cleanup, kill

## Infrastructure as Code (bank_devops.py)

- [ ] **MC, easy:** Which statements about Infrastructure as Code are true?
  - A. **Infrastructure is described in files**
  - B. **The files can be versioned in Git**
  - C. **Environments can be recreated the same way**
  - D. Servers are set up by clicking in a console each time
- [ ] **MC, easy:** Which of these are Infrastructure as Code tools?
  - A. **Terraform**
  - B. **AWS CloudFormation**
  - C. **Pulumi**
  - D. Microsoft Excel
- [ ] **MC, easy:** Which are benefits of Infrastructure as Code?
  - A. **Repeatable environments**
  - B. **Changes can be reviewed like code**
  - C. **Fewer manual mistakes**
  - D. No need to understand the cloud
- [ ] **MC, medium:** Which statements about declarative and imperative approaches are true?
  - A. **Declarative describes the desired end state**
  - B. **Imperative lists the steps to run**
  - C. **Terraform is declarative**
  - D. Declarative tools make you write every step
- [ ] **MC, medium:** Which statements about configuration drift are true?
  - A. **Manual changes make the real infrastructure differ from the code**
  - B. **IaC tools can detect it**
  - C. **Making every change through code prevents it**
  - D. It never happens
- [ ] **MC, medium:** Which statements about idempotence in IaC are true?
  - A. **Applying the same code twice gives the same result**
  - B. **It makes reruns safe**
  - C. **Most IaC tools aim for it**
  - D. It creates duplicate resources on each run
- [ ] **MC, medium:** Which help reuse infrastructure code?
  - A. **Modules**
  - B. **Variables**
  - C. **Per-environment configuration that shares the same modules**
  - D. Copying and pasting everything
- [ ] **MC, hard:** Which statements about immutable infrastructure are true?
  - A. **Servers are replaced instead of changed in place**
  - B. **A new image is built for each change**
  - C. **It reduces drift**
  - D. Servers are patched by hand while they run
- [ ] **MC, hard:** Which make IaC changes safer?
  - A. **Reviewing a plan before applying it**
  - B. **Running changes through CI with approvals**
  - C. **Testing in a staging environment first**
  - D. Applying straight to production from a laptop
- [ ] **MC, hard:** Which statements about Ansible are true?
  - A. **It configures servers with YAML playbooks**
  - B. **It connects over SSH without an agent**
  - C. **It aims for idempotent tasks**
  - D. It works only on Windows
- [ ] **Blank, easy:** Managing servers and networks through code files is called Infrastructure as ________.  
  Accepted: Code
- [ ] **Blank, easy:** AWS's own Infrastructure as Code service is AWS ________.  
  Accepted: CloudFormation
- [ ] **Blank, easy:** Infrastructure code should be kept in version control such as ________.  
  Accepted: Git
- [ ] **Blank, medium:** Describing the desired end state instead of the steps is the ________ approach.  
  Accepted: declarative
- [ ] **Blank, medium:** When the real infrastructure no longer matches the code, it is called configuration ________.  
  Accepted: drift
- [ ] **Blank, medium:** Reusable bundles of infrastructure code are called ________.  
  Accepted: modules
- [ ] **Blank, medium:** Ansible describes configuration in files called ________.  
  Accepted: playbooks
- [ ] **Blank, hard:** Replacing servers instead of changing them in place is called ________ infrastructure.  
  Accepted: immutable
- [ ] **Blank, hard:** A tool that makes no extra changes when the same code is applied twice is ________.  
  Accepted: idempotent
- [ ] **Blank, hard:** Managing infrastructure through Git pull requests that a tool applies automatically is called ________.  
  Accepted: GitOps
- [ ] **Brief, easy:** What is Infrastructure as Code, and why use it?
  - Key fact: infrastructure such as servers and networks is described in code files
  - Key fact: the files are versioned and reviewed like application code
  - Key fact: environments can be created again in exactly the same way, with fewer manual mistakes
  - Keywords (grading without AI): code, version control, repeatable, review, automation
- [ ] **Brief, medium:** What is the difference between declarative and imperative Infrastructure as Code?
  - Key fact: declarative tools such as Terraform describe the end state you want
  - Key fact: the tool works out what to create, change or delete to reach that state
  - Key fact: imperative scripts list each step and must handle the current state themselves
  - Keywords (grading without AI): declarative, imperative, desired state, Terraform, steps
- [ ] **Brief, medium:** What is configuration drift, and how do you prevent it?
  - Key fact: drift happens when someone changes infrastructure by hand so it no longer matches the code
  - Key fact: IaC tools detect it by comparing the real resources with the code, for example with a plan
  - Key fact: prevent it by making all changes through code and limiting console access
  - Keywords (grading without AI): drift, manual change, plan, detect, access
- [ ] **Brief, hard:** How would you organize Infrastructure as Code for several environments?
  - Key fact: put the shared parts into reusable modules
  - Key fact: give each environment its own configuration and state, using those modules with different variables
  - Key fact: promote changes from dev to staging to production through CI with reviewed plans
  - Keywords (grading without AI): modules, environment, state, variables, promotion
- [ ] **Brief, hard:** What is immutable infrastructure, and what are its benefits?
  - Key fact: servers are never changed in place; a new image is built and deployed for each change
  - Key fact: old servers are replaced, so every server matches a known version
  - Key fact: it removes drift and makes rollback as simple as deploying the previous image
  - Keywords (grading without AI): immutable, image, replace, drift, rollback

## Terraform (bank_devops.py)

- [ ] **MC, easy:** Which statements about Terraform are true?
  - A. **It describes infrastructure in HCL files**
  - B. **It works with many cloud providers**
  - C. **It is declarative**
  - D. It works only with AWS
- [ ] **MC, easy:** Which of these are Terraform commands?
  - A. **terraform init**
  - B. **terraform plan**
  - C. **terraform apply**
  - D. terraform compile
- [ ] **MC, easy:** Which statements about Terraform providers are true?
  - A. **A provider lets Terraform talk to a platform such as AWS**
  - B. **terraform init downloads the providers**
  - C. **Each provider offers resource types**
  - D. Users write providers in YAML
- [ ] **MC, medium:** Which statements about Terraform state are true?
  - A. **It maps the code to real resources**
  - B. **Terraform compares it with the code to plan changes**
  - C. **It can contain secrets**
  - D. It should often be edited by hand
- [ ] **MC, medium:** Which statements about remote state are true?
  - A. **It keeps the state in a shared place such as S3**
  - B. **Locking stops two people applying at the same time**
  - C. **It lets a team work together**
  - D. It should be committed to Git
- [ ] **MC, medium:** Which statements about variables and outputs are true?
  - A. **Variables make modules configurable**
  - B. **Outputs expose values such as an IP address**
  - C. **tfvars files can set variable values**
  - D. Outputs create new resources
- [ ] **MC, medium:** Which statements about terraform plan are true?
  - A. **It shows what will be created, changed or destroyed**
  - B. **It does not change real resources**
  - C. **It should be reviewed before apply**
  - D. It deletes the state
- [ ] **MC, hard:** Which statements about Terraform modules are true?
  - A. **A module is a reusable group of resources**
  - B. **The root module calls child modules**
  - C. **Modules can come from a registry**
  - D. Modules cannot take variables
- [ ] **MC, hard:** Which statements about count and for_each are true?
  - A. **Both create several instances of a resource**
  - B. **for_each uses the keys of a map or a set**
  - C. **Removing an item from a count list can shift and recreate the others**
  - D. count and for_each always behave the same
- [ ] **MC, hard:** Which statements about importing existing resources are true?
  - A. **terraform import brings an existing resource under management**
  - B. **Import blocks can do it from the configuration**
  - C. **You still need to write matching code**
  - D. Import deletes and recreates the resource
- [ ] **Blank, easy:** Terraform files are written in a language called ________.  
  Accepted: HCL
- [ ] **Blank, easy:** The command terraform ________ downloads providers and sets up a working folder.  
  Accepted: init
- [ ] **Blank, easy:** The command terraform ________ makes the planned changes to real infrastructure.  
  Accepted: apply
- [ ] **Blank, medium:** terraform ________ shows the changes Terraform would make without making them.  
  Accepted: plan
- [ ] **Blank, medium:** Terraform records the real resources it manages in a ________ file.  
  Accepted: state
- [ ] **Blank, medium:** A plugin that lets Terraform manage a platform such as AWS is a ________.  
  Accepted: provider
- [ ] **Blank, medium:** Values a module returns, such as an IP address, are called ________.  
  Accepted: outputs
- [ ] **Blank, hard:** Remote state uses ________ so two people cannot apply changes at the same time.  
  Accepted: locking, state locking
- [ ] **Blank, hard:** The meta-argument ________ creates one instance of a resource per key in a map.  
  Accepted: for_each
- [ ] **Blank, hard:** terraform ________ brings an existing resource under Terraform's management.  
  Accepted: import
- [ ] **Brief, easy:** What is Terraform, and what is its basic workflow?
  - Key fact: Terraform describes infrastructure as code in HCL and supports many providers
  - Key fact: terraform init sets up the providers, plan shows the changes and apply makes them
  - Key fact: it keeps a state file that maps the code to the real resources
  - Keywords (grading without AI): HCL, init, plan, apply, state
- [ ] **Brief, medium:** What is Terraform state, and why should it be remote?
  - Key fact: state records which real resources belong to which code
  - Key fact: a remote backend such as S3 lets the whole team use the same state
  - Key fact: locking stops two applies at once, and the state must be protected because it can hold secrets
  - Keywords (grading without AI): state, remote backend, S3, locking, secrets
- [ ] **Brief, medium:** How do Terraform modules help?
  - Key fact: a module groups resources into a reusable unit with inputs and outputs
  - Key fact: the same module can build dev, staging and production with different variables
  - Key fact: modules keep the code shorter and consistent across teams
  - Keywords (grading without AI): module, reusable, variables, outputs, environments
- [ ] **Brief, hard:** What is the difference between count and for_each in Terraform?
  - Key fact: both create several copies of a resource
  - Key fact: count uses numeric indexes, so removing an item in the middle shifts and may recreate the rest
  - Key fact: for_each uses stable keys from a map or set, so items can be added or removed safely
  - Keywords (grading without AI): count, for_each, index, key, recreate
- [ ] **Brief, hard:** How would you run Terraform safely in a team?
  - Key fact: use remote state with locking and keep it out of Git
  - Key fact: run plan in CI on every pull request and have someone review it before apply
  - Key fact: pin provider and module versions, and keep each environment in its own state
  - Keywords (grading without AI): remote state, locking, CI, review, version pinning

## AWS (bank_devops.py)

- [ ] **MC, easy:** Which of these are AWS compute services?
  - A. **Amazon EC2**
  - B. **AWS Lambda**
  - C. **Amazon ECS**
  - D. Amazon S3
- [ ] **MC, easy:** Which of these are AWS database services?
  - A. **Amazon RDS**
  - B. **Amazon DynamoDB**
  - C. **Amazon Aurora**
  - D. Amazon CloudFront
- [ ] **MC, easy:** Which statements about AWS regions and availability zones are true?
  - A. **A region is a geographic area**
  - B. **A region has several availability zones**
  - C. **Spreading across zones improves availability**
  - D. One availability zone covers the whole world
- [ ] **MC, medium:** Which statements about IAM are true?
  - A. **Users, groups and roles get permissions through policies**
  - B. **Roles give temporary credentials to services**
  - C. **Least privilege is the best practice**
  - D. The root account should be used every day
- [ ] **MC, medium:** Which statements about a VPC are true?
  - A. **It is a private network inside AWS**
  - B. **It contains public and private subnets**
  - C. **Security groups control traffic to instances**
  - D. Every VPC is open to the internet by default
- [ ] **MC, medium:** Which statements about AWS Lambda are true?
  - A. **It runs code without you managing servers**
  - B. **It is billed per request and by run time**
  - C. **It scales automatically**
  - D. It is best for processes that run for days
- [ ] **MC, medium:** Which statements about Amazon S3 are true?
  - A. **It stores objects in buckets**
  - B. **It is highly durable**
  - C. **It can host static websites**
  - D. It is a relational database
- [ ] **MC, hard:** Which statements about security groups and network ACLs are true?
  - A. **Security groups are stateful**
  - B. **Network ACLs are stateless**
  - C. **Network ACLs apply at the subnet level**
  - D. Security groups can only deny traffic
- [ ] **MC, hard:** Which help an application on AWS stay available?
  - A. **Instances in several availability zones**
  - B. **An Application Load Balancer with health checks**
  - C. **An Auto Scaling group**
  - D. A single instance with no backup
- [ ] **MC, hard:** Which statements about the AWS shared responsibility model are true?
  - A. **AWS secures the underlying infrastructure**
  - B. **Customers secure their data and configuration**
  - C. **Customers manage their IAM permissions**
  - D. AWS is responsible for every misconfigured bucket
- [ ] **Blank, easy:** AWS's virtual server service is Amazon ________.  
  Accepted: EC2
- [ ] **Blank, easy:** AWS's service for running code without servers is AWS ________.  
  Accepted: Lambda
- [ ] **Blank, easy:** A geographic area with several AWS data centres is called a ________.  
  Accepted: region
- [ ] **Blank, medium:** AWS's service for managing users and permissions is ________.  
  Accepted: IAM
- [ ] **Blank, medium:** A private network you define in AWS is a ________.  
  Accepted: VPC
- [ ] **Blank, medium:** A virtual firewall that controls traffic to an EC2 instance is a security ________.  
  Accepted: group
- [ ] **Blank, medium:** AWS's managed relational database service is Amazon ________.  
  Accepted: RDS
- [ ] **Blank, hard:** Giving each user or service only the permissions it needs is the principle of least ________.  
  Accepted: privilege
- [ ] **Blank, hard:** AWS's content delivery network is Amazon ________.  
  Accepted: CloudFront
- [ ] **Blank, hard:** AWS's service for metrics, logs and alarms is Amazon ________.  
  Accepted: CloudWatch
- [ ] **Brief, easy:** Which core AWS services does a web app usually use?
  - Key fact: EC2, ECS or Lambda to run the code
  - Key fact: RDS or DynamoDB for data, and S3 for files
  - Key fact: a VPC, a load balancer and IAM for networking and access
  - Keywords (grading without AI): EC2, Lambda, RDS, S3, VPC
- [ ] **Brief, medium:** How does IAM control access in AWS?
  - Key fact: users, groups and roles get permissions through JSON policies
  - Key fact: roles give temporary credentials to services such as EC2 or Lambda
  - Key fact: grant least privilege and avoid using the root account
  - Keywords (grading without AI): IAM, policy, role, least privilege, root
- [ ] **Brief, medium:** When would you use Lambda instead of EC2?
  - Key fact: Lambda suits short, event-driven tasks and traffic that comes in bursts
  - Key fact: it scales automatically and you pay only while the code runs
  - Key fact: EC2 suits long-running processes, special software or a steady heavy load
  - Keywords (grading without AI): Lambda, EC2, serverless, event-driven, cost
- [ ] **Brief, hard:** How would you design a highly available web app on AWS?
  - Key fact: run the app in at least two availability zones behind an Application Load Balancer
  - Key fact: use an Auto Scaling group with health checks to replace failed instances
  - Key fact: use a Multi-AZ database and keep static files in S3 behind CloudFront
  - Keywords (grading without AI): availability zone, load balancer, Auto Scaling, Multi-AZ, CloudFront
- [ ] **Brief, hard:** Explain public and private subnets in a VPC.
  - Key fact: a public subnet has a route to an internet gateway
  - Key fact: a private subnet has no direct internet route, so databases and app servers go there
  - Key fact: private resources reach the internet through a NAT gateway, and security groups control the traffic
  - Keywords (grading without AI): public subnet, private subnet, internet gateway, NAT gateway, security group

## Monitoring (bank_devops.py)

- [ ] **MC, easy:** Which are the three pillars of observability?
  - A. **Metrics**
  - B. **Logs**
  - C. **Traces**
  - D. Backups
- [ ] **MC, easy:** Which of these are monitoring tools?
  - A. **Prometheus**
  - B. **Grafana**
  - C. **Datadog**
  - D. Figma
- [ ] **MC, easy:** Which are useful metrics for a web service?
  - A. **Request rate**
  - B. **Error rate**
  - C. **Latency**
  - D. The number of developers
- [ ] **MC, medium:** Which statements about the four golden signals are true?
  - A. **They are latency, traffic, errors and saturation**
  - B. **They come from Google's SRE practice**
  - C. **They give a quick view of a service's health**
  - D. They apply only to databases
- [ ] **MC, medium:** Which statements about alerts are true?
  - A. **Alerts should point to a problem users feel**
  - B. **Every alert should need action**
  - C. **Too many alerts cause alert fatigue**
  - D. Every metric change should raise an alert
- [ ] **MC, medium:** Which statements about percentiles are true?
  - A. **p99 latency shows the slowest 1% of requests**
  - B. **Averages can hide slow requests**
  - C. **p50 is the median**
  - D. p99 is always lower than the average
- [ ] **MC, medium:** Which statements about structured logging are true?
  - A. **Log lines are written as fields, for example as JSON**
  - B. **They are easier to search and filter**
  - C. **Request IDs link related lines**
  - D. Free text is always easier to query
- [ ] **MC, hard:** Which statements about SLIs, SLOs and SLAs are true?
  - A. **An SLI is a measured indicator such as the success rate**
  - B. **An SLO is a target for an SLI**
  - C. **An SLA is a contract with consequences**
  - D. They all mean the same thing
- [ ] **MC, hard:** Which statements about error budgets are true?
  - A. **They are the failure allowed under an SLO**
  - B. **Spending them too fast can pause risky releases**
  - C. **They balance speed and reliability**
  - D. They require 100% uptime
- [ ] **MC, hard:** Which statements about distributed tracing are true?
  - A. **A trace follows one request across services**
  - B. **Spans show the time spent in each step**
  - C. **OpenTelemetry is a standard for it**
  - D. It replaces all metrics
- [ ] **Blank, easy:** Numbers measured over time, such as the request rate, are called ________.  
  Accepted: metrics
- [ ] **Blank, easy:** ________ is a popular open-source tool for dashboards.  
  Accepted: Grafana
- [ ] **Blank, easy:** ________ is an open-source system that collects and stores metrics.  
  Accepted: Prometheus
- [ ] **Blank, medium:** The four golden signals are latency, traffic, errors and ________.  
  Accepted: saturation
- [ ] **Blank, medium:** Too many noisy alerts lead to alert ________.  
  Accepted: fatigue
- [ ] **Blank, medium:** The latency below which 99% of requests finish is the ________ latency.  
  Accepted: p99, 99th percentile
- [ ] **Blank, medium:** Following one request across several services is called distributed ________.  
  Accepted: tracing
- [ ] **Blank, hard:** A target for a service level indicator, such as 99.9% success, is an ________.  
  Accepted: SLO, service level objective
- [ ] **Blank, hard:** The amount of failure allowed under an SLO is the error ________.  
  Accepted: budget
- [ ] **Blank, hard:** The open standard for collecting traces, metrics and logs is ________.  
  Accepted: OpenTelemetry
- [ ] **Brief, easy:** What is the difference between monitoring and observability?
  - Key fact: monitoring watches known metrics and alerts when they cross thresholds
  - Key fact: observability lets you ask new questions about a system from what it outputs
  - Key fact: it relies on metrics, logs and traces together
  - Keywords (grading without AI): monitoring, observability, metrics, logs, traces
- [ ] **Brief, medium:** What makes a good alert?
  - Key fact: it fires on symptoms users feel, such as errors or slow responses
  - Key fact: it is actionable and links to a runbook
  - Key fact: it is rare enough that people trust it, which avoids alert fatigue
  - Keywords (grading without AI): symptom, actionable, runbook, alert fatigue, threshold
- [ ] **Brief, medium:** Why use percentiles instead of averages for latency?
  - Key fact: an average hides the slow requests that some users get
  - Key fact: p95 or p99 shows what the slowest users experience
  - Key fact: targets and alerts on p99 catch problems that an average misses
  - Keywords (grading without AI): percentile, p99, average, latency, tail
- [ ] **Brief, hard:** Explain SLIs, SLOs and error budgets.
  - Key fact: an SLI measures service quality, such as the share of successful requests
  - Key fact: an SLO sets a target for it, such as 99.9% over 30 days
  - Key fact: the error budget is the failure allowed; when it runs low, the team slows releases and works on reliability
  - Keywords (grading without AI): SLI, SLO, error budget, target, reliability
- [ ] **Brief, hard:** Users say the app is slow. How would you investigate with monitoring tools?
  - Key fact: check dashboards for latency, errors and traffic to see when and where it started
  - Key fact: use traces to find which service or call adds the time
  - Key fact: read the logs and resource metrics of that component, such as CPU, memory or database queries
  - Keywords (grading without AI): latency, dashboard, trace, logs, saturation

## Shell Scripting (bank_devops.py)

- [ ] **MC, easy:** Which statements about shell scripts are true?
  - A. **They automate command-line tasks**
  - B. **They usually start with a shebang line**
  - C. **They must be executable or run with bash**
  - D. They are compiled before they run
- [ ] **MC, easy:** Which statements about shell variables are true?
  - A. **name=value sets a variable**
  - B. **$name reads it**
  - C. **Spaces around = break the assignment**
  - D. Variables need a type declaration
- [ ] **MC, easy:** Which commands are often used in shell scripts?
  - A. **echo**
  - B. **grep**
  - C. **sed**
  - D. javac
- [ ] **MC, medium:** Which statements about exit codes are true?
  - A. **0 means success**
  - B. **A non-zero code means failure**
  - C. **$? holds the last exit code**
  - D. Exit codes exist only on Windows
- [ ] **MC, medium:** Which statements about quoting in bash are true?
  - A. **Double quotes allow variable expansion**
  - B. **Single quotes keep text literal**
  - C. **Quoting variables prevents word splitting**
  - D. Quotes are never needed
- [ ] **MC, medium:** Which statements about pipes and redirection are true?
  - A. **| sends one command's output to the next**
  - B. **> writes output to a file, overwriting it**
  - C. **>> appends to a file**
  - D. < sends a command's output into a file
- [ ] **MC, medium:** Which of these are loops in bash?
  - A. **for**
  - B. **while**
  - C. **until**
  - D. foreach-parallel
- [ ] **MC, hard:** Which statements about set -euo pipefail are true?
  - A. **-e stops the script on an error**
  - B. **-u treats unset variables as errors**
  - C. **pipefail makes a pipeline fail if any command in it fails**
  - D. It makes the script ignore errors
- [ ] **MC, hard:** Which statements about cron are true?
  - A. **It runs commands on a schedule**
  - B. **A crontab line has five time fields**
  - C. **Scripts run by cron get a minimal environment**
  - D. cron runs only once, at boot
- [ ] **MC, hard:** Which make shell scripts safer?
  - A. **Quoting variables**
  - B. **Checking that required arguments are set**
  - C. **Using mktemp for temporary files**
  - D. Running every script as root
- [ ] **Blank, easy:** The first line #!/bin/bash of a script is called the ________.  
  Accepted: shebang
- [ ] **Blank, easy:** The command ________ prints text to the terminal.  
  Accepted: echo
- [ ] **Blank, easy:** The command chmod +________ script.sh makes a script executable.  
  Accepted: x
- [ ] **Blank, medium:** In bash, the special variable $? holds the exit ________ of the last command.  
  Accepted: code, status
- [ ] **Blank, medium:** The operator ________ appends output to a file instead of overwriting it.  
  Accepted: >>
- [ ] **Blank, medium:** In bash, the first argument passed to a script is read with $________.  
  Accepted: 1
- [ ] **Blank, medium:** The ________ command replaces text in a stream, as in s/old/new/.  
  Accepted: sed
- [ ] **Blank, hard:** The option set -o ________ makes a pipeline fail when any command in it fails.  
  Accepted: pipefail
- [ ] **Blank, hard:** The scheduler that runs commands at set times on Linux is ________.  
  Accepted: cron
- [ ] **Blank, hard:** The command ________ creates a safe, unique temporary file.  
  Accepted: mktemp
- [ ] **Brief, easy:** What is a shell script, and when would you write one?
  - Key fact: a shell script is a file of commands that the shell runs in order
  - Key fact: it automates repeated tasks such as backups, deployments or log cleanup
  - Key fact: it starts with a shebang line and can use variables, conditions and loops
  - Keywords (grading without AI): script, automate, shebang, variables, loop
- [ ] **Brief, medium:** How do you handle errors in a bash script?
  - Key fact: check exit codes, where 0 means success
  - Key fact: use set -euo pipefail so the script stops on errors and unset variables
  - Key fact: print clear messages to stderr and exit with a non-zero code on failure
  - Keywords (grading without AI): exit code, set -e, pipefail, stderr, error
- [ ] **Brief, medium:** Why should you quote variables in shell scripts?
  - Key fact: unquoted variables are split on spaces and expanded as wildcards
  - Key fact: a file name with a space can then break a command or hit the wrong files
  - Key fact: a variable inside double quotes stays one argument with its exact value
  - Keywords (grading without AI): quote, word splitting, space, glob, argument
- [ ] **Brief, hard:** How would you write a script that safely deletes log files older than 7 days?
  - Key fact: use find with -mtime +7 and -name so it matches only old log files
  - Key fact: print what would be deleted first, then delete the matched files
  - Key fact: quote paths, check that the folder exists, and log what was removed
  - Keywords (grading without AI): find, mtime, delete, dry run, quote
- [ ] **Brief, hard:** How do you schedule a script with cron and make it reliable?
  - Key fact: add a crontab line with the five time fields and the full path of the script
  - Key fact: cron gives a minimal environment, so set PATH and use absolute paths
  - Key fact: send output to a log file, alert on failures and stop runs from overlapping with a lock
  - Keywords (grading without AI): crontab, absolute path, PATH, log, lock

## Jenkins (bank_devops.py)

- [ ] **MC, easy:** Which statements about Jenkins are true?
  - A. **It is an open-source automation server**
  - B. **It runs CI/CD pipelines**
  - C. **It has many plugins**
  - D. It is a cloud provider
- [ ] **MC, easy:** What can trigger a Jenkins job?
  - A. **A push to Git through a webhook**
  - B. **A schedule**
  - C. **A manual click**
  - D. Opening the Jenkins log file
- [ ] **MC, easy:** Which statements about a Jenkinsfile are true?
  - A. **It defines a pipeline as code**
  - B. **It lives in the repository**
  - C. **It uses Groovy syntax**
  - D. It can be edited only in the web UI
- [ ] **MC, medium:** Which statements about declarative pipelines are true?
  - A. **They use a pipeline block with stages and steps**
  - B. **They are easier to read than scripted pipelines**
  - C. **post blocks run after stages on success or failure**
  - D. They cannot run tests
- [ ] **MC, medium:** Which statements about Jenkins agents are true?
  - A. **Agents run the build work**
  - B. **The controller schedules jobs on agents**
  - C. **Agents can be containers or machines**
  - D. Every build must run on the controller
- [ ] **MC, medium:** Which statements about Jenkins credentials are true?
  - A. **They are kept in the credentials store**
  - B. **withCredentials makes them available to steps**
  - C. **They are masked in logs**
  - D. They should be written in the Jenkinsfile
- [ ] **MC, medium:** Which statements about multibranch pipelines are true?
  - A. **They create a pipeline for each branch that has a Jenkinsfile**
  - B. **They can build pull requests**
  - C. **They find new branches automatically**
  - D. They work for only one branch
- [ ] **MC, hard:** Which make Jenkins builds faster?
  - A. **Running stages in parallel**
  - B. **Caching dependencies**
  - C. **Using more agents**
  - D. Running everything on the controller
- [ ] **MC, hard:** Which statements about shared libraries are true?
  - A. **They hold reusable pipeline code**
  - B. **Many Jenkinsfiles can use them**
  - C. **They are versioned in Git**
  - D. They replace all plugins
- [ ] **MC, hard:** Which keep a Jenkins server secure?
  - A. **Keeping Jenkins and its plugins updated**
  - B. **Not running builds on the controller**
  - C. **Role-based access control**
  - D. Allowing anonymous admin access
- [ ] **Blank, easy:** A pipeline defined as code in Jenkins lives in a file named ________.  
  Accepted: Jenkinsfile
- [ ] **Blank, easy:** Jenkins adds features through ________.  
  Accepted: plugins
- [ ] **Blank, easy:** Git can trigger a Jenkins build on every push through a ________.  
  Accepted: webhook
- [ ] **Blank, medium:** In a declarative pipeline, the work is split into ________, such as Build and Test.  
  Accepted: stages
- [ ] **Blank, medium:** The machines or containers that run Jenkins builds are called ________.  
  Accepted: agents, nodes
- [ ] **Blank, medium:** The ________ block runs steps after a stage or pipeline, for example on failure.  
  Accepted: post
- [ ] **Blank, medium:** A Jenkinsfile is written in ________ syntax.  
  Accepted: Groovy
- [ ] **Blank, hard:** Reusable pipeline code shared by many Jenkinsfiles lives in a shared ________.  
  Accepted: library
- [ ] **Blank, hard:** A ________ pipeline creates a job for every branch that has a Jenkinsfile.  
  Accepted: multibranch
- [ ] **Blank, hard:** The step ________ makes stored credentials available inside a block.  
  Accepted: withCredentials
- [ ] **Brief, easy:** What is Jenkins used for?
  - Key fact: Jenkins is an open-source server that automates builds, tests and deployments
  - Key fact: jobs start on a Git push, on a schedule or by hand
  - Key fact: a Jenkinsfile defines the pipeline as code in the repository
  - Keywords (grading without AI): Jenkins, CI/CD, Jenkinsfile, trigger, automation
- [ ] **Brief, medium:** What is the difference between declarative and scripted Jenkins pipelines?
  - Key fact: declarative pipelines use a fixed structure of pipeline, stages and steps
  - Key fact: scripted pipelines are plain Groovy code and more flexible
  - Key fact: declarative is easier to read and is recommended for most cases
  - Keywords (grading without AI): declarative, scripted, Groovy, stages, structure
- [ ] **Brief, medium:** How do you keep secrets safe in Jenkins?
  - Key fact: keep them in the Jenkins credentials store, not in the Jenkinsfile
  - Key fact: use withCredentials so they exist only where needed and are masked in logs
  - Key fact: limit who can see and use credentials with access control
  - Keywords (grading without AI): credentials, withCredentials, mask, access control, Jenkinsfile
- [ ] **Brief, hard:** How would you speed up a slow Jenkins pipeline?
  - Key fact: run independent stages in parallel on several agents
  - Key fact: cache dependencies and build outputs between runs
  - Key fact: run only the tests a change can affect where that is safe, and keep build work off the controller
  - Keywords (grading without AI): parallel, agents, cache, tests, controller
- [ ] **Brief, hard:** How would you share pipeline logic across many repositories in Jenkins?
  - Key fact: put the common steps in a shared library stored in Git
  - Key fact: each Jenkinsfile loads the library and calls its steps
  - Key fact: version the library so changes can be tested before every pipeline uses them
  - Keywords (grading without AI): shared library, Git, Jenkinsfile, reuse, version

## Networking Basics (bank_devops.py)

- [ ] **MC, easy:** Which of these are layers of the TCP/IP model?
  - A. **Application**
  - B. **Transport**
  - C. **Internet**
  - D. Spreadsheet
- [ ] **MC, easy:** Which statements about IP addresses are true?
  - A. **They identify devices on a network**
  - B. **An IPv4 address has four numbers**
  - C. **IPv6 addresses are longer**
  - D. Every device on the internet uses the same address
- [ ] **MC, easy:** Which protocols are used when browsing the web?
  - A. **HTTP**
  - B. **HTTPS**
  - C. **DNS**
  - D. Bluetooth
- [ ] **MC, medium:** Which statements about TCP and UDP are true?
  - A. **TCP is connection-based and reliable**
  - B. **UDP sends packets without a connection**
  - C. **UDP suits video calls and games**
  - D. UDP guarantees delivery in order
- [ ] **MC, medium:** Which statements about DNS are true?
  - A. **It turns domain names into IP addresses**
  - B. **An A record points to an IPv4 address**
  - C. **A CNAME record points one name at another name**
  - D. DNS answers are never cached
- [ ] **MC, medium:** Which of these are correct default ports?
  - A. **80 for HTTP**
  - B. **443 for HTTPS**
  - C. **22 for SSH**
  - D. 25 for HTTPS
- [ ] **MC, medium:** Which statements about private IP ranges are true?
  - A. **10.0.0.0/8 is private**
  - B. **192.168.0.0/16 is private**
  - C. **Private addresses are not routed on the public internet**
  - D. 8.8.8.8 is a private address
- [ ] **MC, hard:** Which statements about CIDR notation are true?
  - A. **/24 leaves 8 bits for hosts**
  - B. **A /24 network has 256 addresses**
  - C. **A smaller prefix number means a larger network**
  - D. /32 covers the whole internet
- [ ] **MC, hard:** Which statements about NAT are true?
  - A. **It maps private addresses to a public address**
  - B. **It lets many devices share one public IP**
  - C. **Outside hosts cannot start connections to private hosts without port forwarding**
  - D. It encrypts traffic
- [ ] **MC, hard:** What happens when you open a website in a browser?
  - A. **The browser looks up the IP address with DNS**
  - B. **It opens a TCP connection and, for HTTPS, does a TLS handshake**
  - C. **It sends an HTTP request and renders the response**
  - D. The server sends the whole internet to the browser
- [ ] **Blank, easy:** The system that turns domain names into IP addresses is ________.  
  Accepted: DNS
- [ ] **Blank, easy:** HTTPS uses port ________ by default.  
  Accepted: 443
- [ ] **Blank, easy:** Secure remote login to a server uses ________ on port 22.  
  Accepted: SSH
- [ ] **Blank, medium:** The reliable, connection-based transport protocol is ________.  
  Accepted: TCP
- [ ] **Blank, medium:** A DNS ________ record maps a name to an IPv4 address.  
  Accepted: A
- [ ] **Blank, medium:** The protocol that encrypts HTTPS traffic is ________.  
  Accepted: TLS, SSL
- [ ] **Blank, medium:** A device that forwards packets between networks is a ________.  
  Accepted: router
- [ ] **Blank, hard:** A /24 network contains ________ IP addresses.  
  Accepted: 256
- [ ] **Blank, hard:** Mapping many private addresses to one public address is network address ________.  
  Accepted: translation
- [ ] **Blank, hard:** TCP opens a connection with a three-way ________.  
  Accepted: handshake
- [ ] **Brief, easy:** What happens when you type a website address into a browser?
  - Key fact: DNS turns the name into an IP address
  - Key fact: the browser opens a TCP connection and, for HTTPS, does a TLS handshake
  - Key fact: it sends an HTTP request and renders the HTML, CSS and JavaScript it gets back
  - Keywords (grading without AI): DNS, TCP, TLS, HTTP, render
- [ ] **Brief, medium:** What is the difference between TCP and UDP?
  - Key fact: TCP sets up a connection and guarantees delivery and order
  - Key fact: UDP sends packets with no connection and no delivery guarantee
  - Key fact: TCP suits web pages and file transfers, UDP suits video calls, games and DNS lookups
  - Keywords (grading without AI): TCP, UDP, reliable, connection, latency
- [ ] **Brief, medium:** How does DNS work?
  - Key fact: a resolver asks the root, top-level domain and authoritative servers for a name's address
  - Key fact: records such as A, AAAA and CNAME hold the answers
  - Key fact: answers are cached for their TTL so lookups are fast
  - Keywords (grading without AI): resolver, authoritative, A record, CNAME, TTL
- [ ] **Brief, hard:** Explain CIDR and subnets with an example.
  - Key fact: CIDR writes a network as an address and a prefix length, such as 10.0.1.0/24
  - Key fact: the prefix says how many bits belong to the network; /24 leaves 8 bits, or 256 addresses
  - Key fact: subnets split a larger network, such as a /16, into smaller ones for public and private parts
  - Keywords (grading without AI): CIDR, prefix, subnet, /24, addresses
- [ ] **Brief, hard:** A service cannot reach a database on another server. How would you troubleshoot it?
  - Key fact: check that DNS resolves the host and that ping or traceroute shows a route
  - Key fact: test the port with nc or telnet to see whether it is open
  - Key fact: check firewalls and security groups, and that the database listens on the right address
  - Keywords (grading without AI): DNS, ping, port, firewall, security group

## Azure (bank_devops.py)

- [ ] **MC, easy:** Which of these are Azure compute services?
  - A. **Azure Virtual Machines**
  - B. **Azure App Service**
  - C. **Azure Functions**
  - D. Azure Blob Storage
- [ ] **MC, easy:** Which of these are Azure data services?
  - A. **Azure SQL Database**
  - B. **Azure Cosmos DB**
  - C. **Azure Blob Storage**
  - D. Azure DevOps Boards
- [ ] **MC, easy:** Which statements about resource groups are true?
  - A. **They group related Azure resources**
  - B. **Deleting a group deletes its resources**
  - C. **Permissions can be set on a group**
  - D. A resource can be in several groups at once
- [ ] **MC, medium:** Which statements about Microsoft Entra ID are true?
  - A. **It is Azure's identity service, formerly Azure Active Directory**
  - B. **It manages users and sign-in**
  - C. **It supports single sign-on and MFA**
  - D. It stores virtual machine disks
- [ ] **MC, medium:** Which statements about Azure role-based access control are true?
  - A. **Roles such as Reader and Contributor grant permissions**
  - B. **Roles are assigned at a scope such as a resource group**
  - C. **Least privilege is recommended**
  - D. Everyone should be Owner
- [ ] **MC, medium:** Which statements about Azure Functions are true?
  - A. **They run code in response to events**
  - B. **The consumption plan bills per execution**
  - C. **They scale automatically**
  - D. They are best for long-running servers
- [ ] **MC, medium:** Which statements about Azure regions and availability zones are true?
  - A. **A region is a set of data centres in one area**
  - B. **Availability zones are separate locations within a region**
  - C. **Region pairs help with disaster recovery**
  - D. Every service runs in only one data centre worldwide
- [ ] **MC, hard:** Which statements about managed identities are true?
  - A. **They let Azure resources sign in to other services without stored secrets**
  - B. **A system-assigned identity is tied to one resource**
  - C. **A user-assigned identity can be shared by several resources**
  - D. They require passwords in the code
- [ ] **MC, hard:** Which statements about Azure Key Vault are true?
  - A. **It stores secrets, keys and certificates**
  - B. **Access is controlled with RBAC or access policies**
  - C. **Apps can read secrets with a managed identity**
  - D. It is meant for storing large files
- [ ] **MC, hard:** Which statements about ARM templates and Bicep are true?
  - A. **They define Azure infrastructure as code**
  - B. **Bicep is a simpler language that compiles to ARM templates**
  - C. **Deployments are declarative**
  - D. They work only for virtual machines
- [ ] **Blank, easy:** Azure's serverless compute service is Azure ________.  
  Accepted: Functions
- [ ] **Blank, easy:** A container that groups related Azure resources is a resource ________.  
  Accepted: group
- [ ] **Blank, easy:** Azure's object storage for files is Azure ________ Storage.  
  Accepted: Blob
- [ ] **Blank, medium:** Azure's identity service, formerly Azure Active Directory, is Microsoft ________ ID.  
  Accepted: Entra
- [ ] **Blank, medium:** Azure's service for hosting web apps without managing servers is Azure App ________.  
  Accepted: Service
- [ ] **Blank, medium:** Azure's globally distributed NoSQL database is Azure ________ DB.  
  Accepted: Cosmos
- [ ] **Blank, medium:** Azure controls access with role-based access ________.  
  Accepted: control
- [ ] **Blank, hard:** Azure's service for storing secrets and keys is Azure Key ________.  
  Accepted: Vault
- [ ] **Blank, hard:** An identity that lets an Azure resource reach other services without secrets is a ________ identity.  
  Accepted: managed
- [ ] **Blank, hard:** Azure's simpler infrastructure-as-code language that compiles to ARM templates is ________.  
  Accepted: Bicep
- [ ] **Brief, easy:** Which Azure services might a web app use?
  - Key fact: App Service, Azure Functions or virtual machines to run the code
  - Key fact: Azure SQL Database or Cosmos DB for data, and Blob Storage for files
  - Key fact: Microsoft Entra ID for sign-in and Key Vault for secrets
  - Keywords (grading without AI): App Service, Functions, Azure SQL, Blob Storage, Entra ID
- [ ] **Brief, medium:** How does access control work in Azure?
  - Key fact: Microsoft Entra ID manages identities such as users and service principals
  - Key fact: Azure RBAC assigns roles such as Reader or Contributor at a scope such as a resource group
  - Key fact: grant least privilege and prefer managed identities for apps
  - Keywords (grading without AI): Entra ID, RBAC, role, scope, managed identity
- [ ] **Brief, medium:** When would you use Azure Functions instead of App Service?
  - Key fact: Functions suit short, event-driven tasks such as reacting to a queue message or a file upload
  - Key fact: they scale automatically, and on the consumption plan you pay per run
  - Key fact: App Service suits long-running web apps and APIs with steady traffic
  - Keywords (grading without AI): Functions, App Service, event-driven, consumption plan, scale
- [ ] **Brief, hard:** How would you keep secrets out of the code of an Azure app?
  - Key fact: store secrets, keys and certificates in Azure Key Vault
  - Key fact: give the app a managed identity and grant it access to the vault
  - Key fact: read secrets at runtime or through Key Vault references, and never commit them to Git
  - Keywords (grading without AI): Key Vault, managed identity, secret, RBAC, Git
- [ ] **Brief, hard:** How would you make an Azure application highly available?
  - Key fact: deploy across availability zones in a region
  - Key fact: put a load balancer or Azure Front Door with health probes in front
  - Key fact: use zone-redundant or geo-replicated databases and storage, and plan failover to a paired region
  - Keywords (grading without AI): availability zone, load balancer, Front Door, geo-replication, failover

## Cloud Security (bank_devops.py)

- [ ] **MC, easy:** Which are cloud security best practices?
  - A. **Turning on multi-factor authentication**
  - B. **Giving least-privilege access**
  - C. **Encrypting sensitive data**
  - D. Sharing one admin account across the team
- [ ] **MC, easy:** Which are common causes of cloud data leaks?
  - A. **Storage buckets that anyone can read**
  - B. **Access keys leaked in code**
  - C. **Overly broad permissions**
  - D. Strong encryption
- [ ] **MC, easy:** Which statements about the shared responsibility model are true?
  - A. **The provider secures the physical data centres**
  - B. **The customer secures their data and access settings**
  - C. **The split differs between IaaS and SaaS**
  - D. The provider is responsible for everything
- [ ] **MC, medium:** Which statements about access keys are true?
  - A. **Long-lived keys are risky if they leak**
  - B. **Roles with temporary credentials are safer**
  - C. **Keys should be rotated**
  - D. Keys belong in the Git repository
- [ ] **MC, medium:** Which statements about encryption are true?
  - A. **Encryption at rest protects stored data**
  - B. **Encryption in transit protects data moving over networks**
  - C. **Key management services control the encryption keys**
  - D. Encryption makes access control unnecessary
- [ ] **MC, medium:** Which help detect threats in the cloud?
  - A. **Audit logs such as CloudTrail**
  - B. **Threat detection services such as GuardDuty**
  - C. **Alerts on unusual activity**
  - D. Turning off logging to save money
- [ ] **MC, medium:** Which statements about network security in the cloud are true?
  - A. **Keep databases in private subnets**
  - B. **Allow only the ports you need in security groups**
  - C. **Put a web application firewall in front of public apps**
  - D. Open every port to make debugging easier
- [ ] **MC, hard:** Which statements about zero trust are true?
  - A. **No user or device is trusted just for being on the network**
  - B. **Every request is authenticated and authorized**
  - C. **Access is granted with least privilege**
  - D. A VPN alone is enough
- [ ] **MC, hard:** Which statements about cloud security posture management are true?
  - A. **It finds misconfigurations such as public buckets**
  - B. **It checks resources against benchmarks such as CIS**
  - C. **It can run continuously**
  - D. It replaces IAM
- [ ] **MC, hard:** Which limit the damage if credentials are stolen?
  - A. **Short-lived credentials**
  - B. **Least-privilege permissions**
  - C. **Multi-factor authentication**
  - D. One key with full admin rights for every service
- [ ] **Blank, easy:** Requiring a second factor such as a code at login is called multi-factor ________.  
  Accepted: authentication
- [ ] **Blank, easy:** Giving users only the permissions they need is the principle of least ________.  
  Accepted: privilege
- [ ] **Blank, easy:** Protecting stored data by scrambling it with a key is called encryption at ________.  
  Accepted: rest
- [ ] **Blank, medium:** AWS's service that records API calls for auditing is AWS ________.  
  Accepted: CloudTrail
- [ ] **Blank, medium:** A ________ application firewall filters malicious HTTP traffic.  
  Accepted: web
- [ ] **Blank, medium:** Cloud services that create and manage encryption keys are called key management ________.  
  Accepted: services, service
- [ ] **Blank, medium:** Regularly replacing access keys and passwords is called key ________.  
  Accepted: rotation
- [ ] **Blank, hard:** The security model that trusts no request by default is called zero ________.  
  Accepted: trust
- [ ] **Blank, hard:** Tools that find cloud misconfigurations belong to cloud security ________ management.  
  Accepted: posture
- [ ] **Blank, hard:** A widely used set of security configuration benchmarks comes from the ________.  
  Accepted: CIS, Center for Internet Security
- [ ] **Brief, easy:** What are the most important basics of cloud security?
  - Key fact: protect accounts with MFA and give least-privilege access
  - Key fact: keep data private and encrypted at rest and in transit
  - Key fact: log activity and watch for misconfigurations such as public buckets
  - Keywords (grading without AI): MFA, least privilege, encryption, logging, misconfiguration
- [ ] **Brief, medium:** Explain the shared responsibility model.
  - Key fact: the cloud provider secures the infrastructure: buildings, hardware and the virtualization layer
  - Key fact: the customer secures what they put in the cloud: data, identities, access settings and configuration
  - Key fact: the split depends on the service type, from IaaS where the customer does more to SaaS where the provider does more
  - Keywords (grading without AI): shared responsibility, provider, customer, IaaS, SaaS
- [ ] **Brief, medium:** How do you protect cloud access keys and credentials?
  - Key fact: avoid long-lived keys and use roles or federated, short-lived credentials
  - Key fact: never commit keys to Git, and scan repositories for leaked secrets
  - Key fact: rotate keys, use MFA and grant only the permissions needed
  - Keywords (grading without AI): access keys, roles, short-lived, secret scanning, rotation
- [ ] **Brief, hard:** What would you do if an access key was leaked publicly?
  - Key fact: disable or delete the key at once, and create a new one where it is needed
  - Key fact: review audit logs such as CloudTrail to see what the key was used for
  - Key fact: remove anything an attacker created, purge the key from history and find out how it leaked
  - Keywords (grading without AI): revoke, rotate, audit logs, CloudTrail, incident
- [ ] **Brief, hard:** What is zero trust, and how would you apply it in the cloud?
  - Key fact: zero trust means no request is trusted just because it comes from inside the network
  - Key fact: every request is authenticated and authorized with least privilege, using identity and device signals
  - Key fact: apply it with strong identity, MFA, small network segments and a log of every access
  - Keywords (grading without AI): zero trust, authentication, least privilege, segmentation, MFA

