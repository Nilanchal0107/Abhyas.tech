-- V2: 42 starter roles (owner_user_id NULL), their aliases and skills. Generated from database-schema.md Section 12 / roles_starter.csv.

INSERT INTO skill (name) VALUES
  ('Data Structures'),
  ('Algorithms'),
  ('Object-Oriented Programming'),
  ('System Design'),
  ('SQL'),
  ('Git'),
  ('REST APIs'),
  ('Unit Testing'),
  ('Design Patterns'),
  ('Debugging'),
  ('Agile'),
  ('Java'),
  ('Spring Boot'),
  ('MySQL'),
  ('Microservices'),
  ('Redis'),
  ('Message Queues'),
  ('Authentication'),
  ('Docker'),
  ('JavaScript'),
  ('TypeScript'),
  ('React'),
  ('HTML'),
  ('CSS'),
  ('Responsive Design'),
  ('Web Accessibility'),
  ('State Management'),
  ('Web Performance'),
  ('Node.js'),
  ('MongoDB'),
  ('Kotlin'),
  ('Swift'),
  ('React Native'),
  ('Mobile UI Design'),
  ('Local Storage'),
  ('App Lifecycle'),
  ('Push Notifications'),
  ('App Store Publishing'),
  ('C#'),
  ('C++'),
  ('Unity'),
  ('Game Physics'),
  ('3D Math'),
  ('Game Loop'),
  ('Graphics Programming'),
  ('Performance Optimization'),
  ('C'),
  ('Microcontrollers'),
  ('RTOS'),
  ('Communication Protocols'),
  ('Hardware Interfacing'),
  ('Memory Management'),
  ('Linux'),
  ('Excel'),
  ('Data Visualization'),
  ('Statistics'),
  ('Python'),
  ('Data Cleaning'),
  ('Dashboards'),
  ('Power BI'),
  ('Tableau'),
  ('Critical Thinking'),
  ('Communication'),
  ('Machine Learning'),
  ('Feature Engineering'),
  ('Model Evaluation'),
  ('Pandas'),
  ('Scikit-learn'),
  ('ETL'),
  ('Data Pipelines'),
  ('Apache Spark'),
  ('Kafka'),
  ('Data Warehousing'),
  ('Airflow'),
  ('Cloud Storage'),
  ('Data Modeling'),
  ('Deep Learning'),
  ('PyTorch'),
  ('Model Deployment'),
  ('MLOps'),
  ('LLM APIs'),
  ('Prompt Engineering'),
  ('Retrieval-Augmented Generation'),
  ('Vector Databases'),
  ('Cost Management'),
  ('DAX'),
  ('Performance Tuning'),
  ('CI/CD'),
  ('Kubernetes'),
  ('Infrastructure as Code'),
  ('Terraform'),
  ('AWS'),
  ('Monitoring'),
  ('Shell Scripting'),
  ('Jenkins'),
  ('Networking Basics'),
  ('Azure'),
  ('Cloud Security'),
  ('Incident Response'),
  ('Capacity Planning'),
  ('Observability'),
  ('Developer Experience'),
  ('Go'),
  ('Build Tools'),
  ('Release Management'),
  ('Version Control Branching'),
  ('Artifact Repositories'),
  ('Automation'),
  ('PostgreSQL'),
  ('Database Backup and Recovery'),
  ('Query Optimization'),
  ('Indexing'),
  ('Database Security'),
  ('Replication'),
  ('TCP/IP'),
  ('Routing and Switching'),
  ('Firewalls'),
  ('VPN'),
  ('DNS'),
  ('Network Monitoring'),
  ('Network Security'),
  ('Troubleshooting'),
  ('Cisco'),
  ('Windows Server'),
  ('Active Directory'),
  ('Backup and Recovery'),
  ('Virtualization'),
  ('Security Basics'),
  ('Ticketing Systems'),
  ('Hardware Support'),
  ('Customer Communication'),
  ('Remote Support'),
  ('Documentation'),
  ('Test Case Design'),
  ('Manual Testing'),
  ('Bug Reporting'),
  ('Test Plans'),
  ('API Testing'),
  ('Regression Testing'),
  ('Exploratory Testing'),
  ('Selenium'),
  ('Test Automation Frameworks'),
  ('JMeter'),
  ('Load Testing'),
  ('Performance Monitoring'),
  ('Bottleneck Analysis'),
  ('Scripting'),
  ('Test Strategy'),
  ('Team Leadership'),
  ('Risk Assessment'),
  ('Defect Management'),
  ('Stakeholder Management'),
  ('Metrics and Reporting'),
  ('Encryption'),
  ('Vulnerability Management'),
  ('Threat Modeling'),
  ('OWASP Top 10'),
  ('Secure Code Review'),
  ('Static Analysis'),
  ('Dynamic Analysis'),
  ('Penetration Testing'),
  ('Burp Suite'),
  ('Metasploit'),
  ('Vulnerability Assessment'),
  ('Report Writing'),
  ('Web Security'),
  ('Social Engineering'),
  ('SIEM'),
  ('Log Analysis'),
  ('Threat Detection'),
  ('Malware Analysis Basics'),
  ('Scalability'),
  ('API Design'),
  ('Cloud Architecture'),
  ('Trade-off Analysis'),
  ('Database Design'),
  ('Technical Documentation'),
  ('Mentoring'),
  ('Integration Patterns'),
  ('Code Review'),
  ('Estimation'),
  ('Conflict Resolution'),
  ('Hiring'),
  ('Performance Reviews'),
  ('Project Planning'),
  ('Roadmapping'),
  ('Budgeting'),
  ('Product Roadmapping'),
  ('User Research'),
  ('Prioritization'),
  ('Requirements Gathering'),
  ('Market Analysis'),
  ('A/B Testing'),
  ('Wireframing'),
  ('User Stories'),
  ('Process Modeling'),
  ('Data Analysis'),
  ('UML'),
  ('Prototyping'),
  ('Figma'),
  ('Usability Testing'),
  ('Design Systems'),
  ('Information Architecture'),
  ('Visual Design'),
  ('Cross-Team Coordination'),
  ('Solidity'),
  ('Smart Contracts'),
  ('Ethereum'),
  ('Cryptography Basics'),
  ('Web3 Libraries'),
  ('Smart Contract Security'),
  ('Distributed Systems'),
  ('Apex'),
  ('Salesforce Lightning'),
  ('SOQL'),
  ('Salesforce Flows'),
  ('Salesforce Data Model'),
  ('Salesforce Integration'),
  ('SAP ABAP'),
  ('SAP Modules'),
  ('Business Process Knowledge'),
  ('SAP Fiori'),
  ('Data Migration'),
  ('UiPath'),
  ('Process Automation'),
  ('Workflow Design'),
  ('Exception Handling'),
  ('Orchestrator');

INSERT INTO role (name, category) VALUES
  ('Software Engineer', 'Software Development'),
  ('Backend Developer', 'Software Development'),
  ('Frontend Developer', 'Software Development'),
  ('Full Stack Developer', 'Software Development'),
  ('Mobile App Developer', 'Software Development'),
  ('Game Developer', 'Software Development'),
  ('Embedded Systems Engineer', 'Software Development'),
  ('Data Analyst', 'Data and AI'),
  ('Data Scientist', 'Data and AI'),
  ('Data Engineer', 'Data and AI'),
  ('Machine Learning Engineer', 'Data and AI'),
  ('AI Engineer', 'Data and AI'),
  ('BI Developer', 'Data and AI'),
  ('DevOps Engineer', 'Cloud and DevOps'),
  ('Cloud Engineer', 'Cloud and DevOps'),
  ('Site Reliability Engineer', 'Cloud and DevOps'),
  ('Platform Engineer', 'Cloud and DevOps'),
  ('Build and Release Engineer', 'Cloud and DevOps'),
  ('Database Administrator', 'Infrastructure and Databases'),
  ('Network Engineer', 'Infrastructure and Databases'),
  ('Systems Administrator', 'Infrastructure and Databases'),
  ('IT Support Engineer', 'Infrastructure and Databases'),
  ('QA Engineer', 'Quality and Testing'),
  ('Test Automation Engineer', 'Quality and Testing'),
  ('Performance Test Engineer', 'Quality and Testing'),
  ('QA Lead', 'Quality and Testing'),
  ('Security Engineer', 'Security'),
  ('Application Security Engineer', 'Security'),
  ('Penetration Tester', 'Security'),
  ('SOC Analyst', 'Security'),
  ('Software Architect', 'Architecture and Leadership'),
  ('Solutions Architect', 'Architecture and Leadership'),
  ('Technical Lead', 'Architecture and Leadership'),
  ('Engineering Manager', 'Architecture and Leadership'),
  ('Product Manager', 'Product and Design'),
  ('Business Analyst', 'Product and Design'),
  ('UI/UX Designer', 'Product and Design'),
  ('Technical Program Manager', 'Product and Design'),
  ('Blockchain Developer', 'Emerging and Specialized'),
  ('Salesforce Developer', 'Emerging and Specialized'),
  ('SAP Consultant', 'Emerging and Specialized'),
  ('RPA Developer', 'Emerging and Specialized');

INSERT INTO role_alias (role_id, alias)
SELECT r.id, v.alias FROM (
SELECT 'Software Engineer' AS role_name, 'SDE' AS alias
 UNION ALL SELECT 'Software Engineer' AS role_name, 'SWE' AS alias
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Software Developer' AS alias
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Programmer' AS alias
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Backend Engineer' AS alias
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Server-Side Developer' AS alias
 UNION ALL SELECT 'Backend Developer' AS role_name, 'API Developer' AS alias
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Frontend Engineer' AS alias
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'UI Developer' AS alias
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Web Developer' AS alias
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Full Stack Engineer' AS alias
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Full-Stack Developer' AS alias
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'MERN Developer' AS alias
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Android Developer' AS alias
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'iOS Developer' AS alias
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Mobile Engineer' AS alias
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'React Native Developer' AS alias
 UNION ALL SELECT 'Game Developer' AS role_name, 'Game Programmer' AS alias
 UNION ALL SELECT 'Game Developer' AS role_name, 'Unity Developer' AS alias
 UNION ALL SELECT 'Game Developer' AS role_name, 'Gameplay Programmer' AS alias
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Firmware Engineer' AS alias
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Embedded Software Engineer' AS alias
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Embedded Developer' AS alias
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Business Data Analyst' AS alias
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Reporting Analyst' AS alias
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Analytics Analyst' AS alias
 UNION ALL SELECT 'Data Scientist' AS role_name, 'DS' AS alias
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Applied Scientist' AS alias
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Research Data Scientist' AS alias
 UNION ALL SELECT 'Data Engineer' AS role_name, 'DE' AS alias
 UNION ALL SELECT 'Data Engineer' AS role_name, 'ETL Developer' AS alias
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Big Data Engineer' AS alias
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Data Pipeline Engineer' AS alias
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'ML Engineer' AS alias
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'MLE' AS alias
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Applied ML Engineer' AS alias
 UNION ALL SELECT 'AI Engineer' AS role_name, 'LLM Engineer' AS alias
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Generative AI Engineer' AS alias
 UNION ALL SELECT 'AI Engineer' AS role_name, 'GenAI Developer' AS alias
 UNION ALL SELECT 'AI Engineer' AS role_name, 'AI Developer' AS alias
 UNION ALL SELECT 'BI Developer' AS role_name, 'Business Intelligence Developer' AS alias
 UNION ALL SELECT 'BI Developer' AS role_name, 'BI Engineer' AS alias
 UNION ALL SELECT 'BI Developer' AS role_name, 'Reporting Developer' AS alias
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'DevOps' AS alias
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'DevOps Developer' AS alias
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'CI/CD Engineer' AS alias
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Cloud Developer' AS alias
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'AWS Engineer' AS alias
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Azure Engineer' AS alias
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Cloud Infrastructure Engineer' AS alias
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'SRE' AS alias
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Reliability Engineer' AS alias
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Production Engineer' AS alias
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Internal Platform Engineer' AS alias
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Developer Platform Engineer' AS alias
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Infrastructure Engineer' AS alias
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Release Engineer' AS alias
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Build Engineer' AS alias
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Release Manager' AS alias
 UNION ALL SELECT 'Database Administrator' AS role_name, 'DBA' AS alias
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Database Engineer' AS alias
 UNION ALL SELECT 'Database Administrator' AS role_name, 'MySQL DBA' AS alias
 UNION ALL SELECT 'Database Administrator' AS role_name, 'SQL DBA' AS alias
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Network Administrator' AS alias
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Network Specialist' AS alias
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Network Support Engineer' AS alias
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'SysAdmin' AS alias
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'System Administrator' AS alias
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Linux Administrator' AS alias
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Windows Administrator' AS alias
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Technical Support Engineer' AS alias
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Desktop Support Engineer' AS alias
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Help Desk Engineer' AS alias
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'L2 Support' AS alias
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Quality Assurance Engineer' AS alias
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Software Tester' AS alias
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Test Engineer' AS alias
 UNION ALL SELECT 'QA Engineer' AS role_name, 'QA Analyst' AS alias
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'SDET' AS alias
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Automation Tester' AS alias
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Software Development Engineer in Test' AS alias
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'QA Automation Engineer' AS alias
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Performance Tester' AS alias
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Load Test Engineer' AS alias
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Performance Engineer' AS alias
 UNION ALL SELECT 'QA Lead' AS role_name, 'Test Lead' AS alias
 UNION ALL SELECT 'QA Lead' AS role_name, 'QA Manager' AS alias
 UNION ALL SELECT 'QA Lead' AS role_name, 'Test Manager' AS alias
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Cybersecurity Engineer' AS alias
 UNION ALL SELECT 'Security Engineer' AS role_name, 'InfoSec Engineer' AS alias
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Information Security Engineer' AS alias
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'AppSec Engineer' AS alias
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Product Security Engineer' AS alias
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Secure Code Reviewer' AS alias
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Pentester' AS alias
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Ethical Hacker' AS alias
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Red Team Engineer' AS alias
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Offensive Security Engineer' AS alias
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Security Analyst' AS alias
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Cybersecurity Analyst' AS alias
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Security Operations Analyst' AS alias
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Blue Team Analyst' AS alias
 UNION ALL SELECT 'Software Architect' AS role_name, 'Technical Architect' AS alias
 UNION ALL SELECT 'Software Architect' AS role_name, 'Application Architect' AS alias
 UNION ALL SELECT 'Software Architect' AS role_name, 'Principal Engineer' AS alias
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Cloud Solutions Architect' AS alias
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Enterprise Architect' AS alias
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Pre-Sales Architect' AS alias
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Tech Lead' AS alias
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Team Lead' AS alias
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Lead Developer' AS alias
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Lead Engineer' AS alias
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'EM' AS alias
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Development Manager' AS alias
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Software Engineering Manager' AS alias
 UNION ALL SELECT 'Product Manager' AS role_name, 'PM' AS alias
 UNION ALL SELECT 'Product Manager' AS role_name, 'Product Owner' AS alias
 UNION ALL SELECT 'Product Manager' AS role_name, 'Associate Product Manager' AS alias
 UNION ALL SELECT 'Product Manager' AS role_name, 'APM' AS alias
 UNION ALL SELECT 'Business Analyst' AS role_name, 'BA' AS alias
 UNION ALL SELECT 'Business Analyst' AS role_name, 'IT Business Analyst' AS alias
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Systems Analyst' AS alias
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Functional Analyst' AS alias
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'UX Designer' AS alias
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'UI Designer' AS alias
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Product Designer' AS alias
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Interaction Designer' AS alias
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'TPM' AS alias
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Program Manager' AS alias
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Technical Project Manager' AS alias
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Project Manager' AS alias
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Smart Contract Developer' AS alias
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Web3 Developer' AS alias
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Solidity Developer' AS alias
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'SFDC Developer' AS alias
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Salesforce Engineer' AS alias
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Apex Developer' AS alias
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SAP Developer' AS alias
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SAP ABAP Developer' AS alias
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SAP Functional Consultant' AS alias
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Robotic Process Automation Developer' AS alias
 UNION ALL SELECT 'RPA Developer' AS role_name, 'UiPath Developer' AS alias
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Automation Developer' AS alias
) v JOIN role r ON r.name = v.role_name AND r.owner_user_id IS NULL;

INSERT INTO role_skill (role_id, skill_id)
SELECT r.id, s.id FROM (
SELECT 'Software Engineer' AS role_name, 'Data Structures' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Algorithms' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Object-Oriented Programming' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'System Design' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Design Patterns' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Debugging' AS skill_name
 UNION ALL SELECT 'Software Engineer' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Java' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Spring Boot' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'MySQL' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Microservices' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Redis' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Message Queues' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Authentication' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'Backend Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'JavaScript' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'TypeScript' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'React' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'HTML' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'CSS' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Responsive Design' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Web Accessibility' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'State Management' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Frontend Developer' AS role_name, 'Web Performance' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'JavaScript' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'React' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Node.js' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'MongoDB' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'HTML' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'CSS' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Authentication' AS skill_name
 UNION ALL SELECT 'Full Stack Developer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Kotlin' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Swift' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'React Native' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Mobile UI Design' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Local Storage' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'App Lifecycle' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Push Notifications' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Mobile App Developer' AS role_name, 'App Store Publishing' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'C#' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'C++' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Unity' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Game Physics' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, '3D Math' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Game Loop' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Data Structures' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Algorithms' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Graphics Programming' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Performance Optimization' AS skill_name
 UNION ALL SELECT 'Game Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'C' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'C++' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Microcontrollers' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'RTOS' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Communication Protocols' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Debugging' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Hardware Interfacing' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Memory Management' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Embedded Systems Engineer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Excel' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Data Visualization' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Statistics' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Data Cleaning' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Dashboards' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Power BI' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Tableau' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Critical Thinking' AS skill_name
 UNION ALL SELECT 'Data Analyst' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Statistics' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Machine Learning' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Data Cleaning' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Feature Engineering' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Model Evaluation' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Data Visualization' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Pandas' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Scikit-learn' AS skill_name
 UNION ALL SELECT 'Data Scientist' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'ETL' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Data Pipelines' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Apache Spark' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Kafka' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Data Warehousing' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Airflow' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Cloud Storage' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Data Modeling' AS skill_name
 UNION ALL SELECT 'Data Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Machine Learning' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Deep Learning' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'PyTorch' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Model Deployment' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Feature Engineering' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Model Evaluation' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'MLOps' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Machine Learning Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'LLM APIs' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Prompt Engineering' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Retrieval-Augmented Generation' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Vector Databases' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Model Evaluation' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Machine Learning' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'AI Engineer' AS role_name, 'Cost Management' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Data Warehousing' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'ETL' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Power BI' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Tableau' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Data Modeling' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Dashboards' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Data Visualization' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'DAX' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Performance Tuning' AS skill_name
 UNION ALL SELECT 'BI Developer' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Kubernetes' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Infrastructure as Code' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Terraform' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'AWS' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Monitoring' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Shell Scripting' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Jenkins' AS skill_name
 UNION ALL SELECT 'DevOps Engineer' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'AWS' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Azure' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Infrastructure as Code' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Terraform' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Cloud Security' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Monitoring' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Cost Management' AS skill_name
 UNION ALL SELECT 'Cloud Engineer' AS role_name, 'Shell Scripting' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Monitoring' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Incident Response' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Kubernetes' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Infrastructure as Code' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Capacity Planning' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Observability' AS skill_name
 UNION ALL SELECT 'Site Reliability Engineer' AS role_name, 'Shell Scripting' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Kubernetes' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Terraform' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'AWS' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Observability' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Developer Experience' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Go' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Platform Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Jenkins' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Build Tools' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Shell Scripting' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Docker' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Release Management' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Version Control Branching' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Artifact Repositories' AS skill_name
 UNION ALL SELECT 'Build and Release Engineer' AS role_name, 'Automation' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'MySQL' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'PostgreSQL' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Database Backup and Recovery' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Query Optimization' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Indexing' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Database Security' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Replication' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Performance Tuning' AS skill_name
 UNION ALL SELECT 'Database Administrator' AS role_name, 'Shell Scripting' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'TCP/IP' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Routing and Switching' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Firewalls' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'VPN' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'DNS' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Network Monitoring' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Network Security' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Troubleshooting' AS skill_name
 UNION ALL SELECT 'Network Engineer' AS role_name, 'Cisco' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Windows Server' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Shell Scripting' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Active Directory' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Backup and Recovery' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Monitoring' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Troubleshooting' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Virtualization' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Security Basics' AS skill_name
 UNION ALL SELECT 'Systems Administrator' AS role_name, 'Automation' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Troubleshooting' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Windows Server' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Active Directory' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Ticketing Systems' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Hardware Support' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Customer Communication' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Remote Support' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Security Basics' AS skill_name
 UNION ALL SELECT 'IT Support Engineer' AS role_name, 'Documentation' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Test Case Design' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Manual Testing' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Bug Reporting' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Test Plans' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'API Testing' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Regression Testing' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Exploratory Testing' AS skill_name
 UNION ALL SELECT 'QA Engineer' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Selenium' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Test Automation Frameworks' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Java' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Python' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'API Testing' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Regression Testing' AS skill_name
 UNION ALL SELECT 'Test Automation Engineer' AS role_name, 'Test Case Design' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'JMeter' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Load Testing' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Performance Monitoring' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Bottleneck Analysis' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'API Testing' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Scripting' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Test Plans' AS skill_name
 UNION ALL SELECT 'Performance Test Engineer' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Test Plans' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Test Strategy' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Team Leadership' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Risk Assessment' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Test Automation Frameworks' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Defect Management' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'Metrics and Reporting' AS skill_name
 UNION ALL SELECT 'QA Lead' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Security Basics' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Network Security' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Cloud Security' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Authentication' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Encryption' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Vulnerability Management' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Incident Response' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Scripting' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Firewalls' AS skill_name
 UNION ALL SELECT 'Security Engineer' AS role_name, 'Threat Modeling' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'OWASP Top 10' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Secure Code Review' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Threat Modeling' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Authentication' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Encryption' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Static Analysis' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Dynamic Analysis' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'CI/CD' AS skill_name
 UNION ALL SELECT 'Application Security Engineer' AS role_name, 'Vulnerability Management' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Penetration Testing' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'OWASP Top 10' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Burp Suite' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Metasploit' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Vulnerability Assessment' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Scripting' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Report Writing' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Web Security' AS skill_name
 UNION ALL SELECT 'Penetration Tester' AS role_name, 'Social Engineering' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'SIEM' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Incident Response' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Log Analysis' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Threat Detection' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Malware Analysis Basics' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Linux' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Security Basics' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Vulnerability Management' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Documentation' AS skill_name
 UNION ALL SELECT 'SOC Analyst' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'System Design' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Design Patterns' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Microservices' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Scalability' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'API Design' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Cloud Architecture' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Trade-off Analysis' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Security Basics' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Database Design' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Technical Documentation' AS skill_name
 UNION ALL SELECT 'Software Architect' AS role_name, 'Mentoring' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Cloud Architecture' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'AWS' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'System Design' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Microservices' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Networking Basics' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Security Basics' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Cost Management' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Technical Documentation' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Integration Patterns' AS skill_name
 UNION ALL SELECT 'Solutions Architect' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'System Design' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Code Review' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Mentoring' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Design Patterns' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Estimation' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Technical Documentation' AS skill_name
 UNION ALL SELECT 'Technical Lead' AS role_name, 'Conflict Resolution' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Team Leadership' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Hiring' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Performance Reviews' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Project Planning' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Mentoring' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Conflict Resolution' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Roadmapping' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Budgeting' AS skill_name
 UNION ALL SELECT 'Engineering Manager' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Product Roadmapping' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'User Research' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Prioritization' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Metrics and Reporting' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Requirements Gathering' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Market Analysis' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'A/B Testing' AS skill_name
 UNION ALL SELECT 'Product Manager' AS role_name, 'Wireframing' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Requirements Gathering' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'User Stories' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Process Modeling' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Documentation' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Wireframing' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Data Analysis' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Business Analyst' AS role_name, 'UML' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Wireframing' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Prototyping' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Figma' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'User Research' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Usability Testing' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Design Systems' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Web Accessibility' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Responsive Design' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Information Architecture' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Visual Design' AS skill_name
 UNION ALL SELECT 'UI/UX Designer' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Project Planning' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Roadmapping' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Risk Assessment' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Stakeholder Management' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Metrics and Reporting' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Cross-Team Coordination' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Technical Documentation' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Budgeting' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'Technical Program Manager' AS role_name, 'Estimation' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Solidity' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Smart Contracts' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Ethereum' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Cryptography Basics' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'JavaScript' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Web3 Libraries' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Smart Contract Security' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Data Structures' AS skill_name
 UNION ALL SELECT 'Blockchain Developer' AS role_name, 'Distributed Systems' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Apex' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Salesforce Lightning' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'SOQL' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Salesforce Flows' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Salesforce Data Model' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'JavaScript' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Salesforce Integration' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Git' AS skill_name
 UNION ALL SELECT 'Salesforce Developer' AS role_name, 'Agile' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SAP ABAP' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SAP Modules' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Business Process Knowledge' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Requirements Gathering' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'SAP Fiori' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Data Migration' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Documentation' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Communication' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Unit Testing' AS skill_name
 UNION ALL SELECT 'SAP Consultant' AS role_name, 'Integration Patterns' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'UiPath' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Process Automation' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Workflow Design' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Exception Handling' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Excel' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'SQL' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'REST APIs' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Requirements Gathering' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Debugging' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Orchestrator' AS skill_name
 UNION ALL SELECT 'RPA Developer' AS role_name, 'Documentation' AS skill_name
) v JOIN role r ON r.name = v.role_name AND r.owner_user_id IS NULL JOIN skill s ON s.name = v.skill_name;
