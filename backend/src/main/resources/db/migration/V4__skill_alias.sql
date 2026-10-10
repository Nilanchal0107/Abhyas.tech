-- V4: skill aliases (skill-bank ticket 04, decision D2). An alias is another way a resume writes a skill, e.g. RESTful for REST APIs.
-- Seeded for the 74 skills of the 11 starter roles. Aliases are matched as whole words, ignoring case (SkillMatcher).

CREATE TABLE skill_alias (
  id       BIGINT NOT NULL AUTO_INCREMENT,
  skill_id BIGINT NOT NULL,
  alias    VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_skill_alias (alias),
  KEY ix_skill_alias_skill (skill_id),
  CONSTRAINT fk_skill_alias_skill FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO skill_alias (skill_id, alias)
SELECT s.id, a.alias FROM skill s JOIN (
  SELECT 'Data Structures' AS skill_name, 'data structure' AS alias
  UNION ALL SELECT 'Data Structures' AS skill_name, 'hash map' AS alias
  UNION ALL SELECT 'Data Structures' AS skill_name, 'linked list' AS alias
  UNION ALL SELECT 'Data Structures' AS skill_name, 'binary tree' AS alias
  UNION ALL SELECT 'Algorithms' AS skill_name, 'algorithm' AS alias
  UNION ALL SELECT 'Algorithms' AS skill_name, 'dynamic programming' AS alias
  UNION ALL SELECT 'Object-Oriented Programming' AS skill_name, 'OOP' AS alias
  UNION ALL SELECT 'Object-Oriented Programming' AS skill_name, 'object oriented' AS alias
  UNION ALL SELECT 'System Design' AS skill_name, 'distributed systems' AS alias
  UNION ALL SELECT 'System Design' AS skill_name, 'scalable architecture' AS alias
  UNION ALL SELECT 'SQL' AS skill_name, 'PostgreSQL' AS alias
  UNION ALL SELECT 'SQL' AS skill_name, 'T-SQL' AS alias
  UNION ALL SELECT 'SQL' AS skill_name, 'PL/SQL' AS alias
  UNION ALL SELECT 'Git' AS skill_name, 'GitHub' AS alias
  UNION ALL SELECT 'Git' AS skill_name, 'GitLab' AS alias
  UNION ALL SELECT 'Git' AS skill_name, 'version control' AS alias
  UNION ALL SELECT 'REST APIs' AS skill_name, 'RESTful' AS alias
  UNION ALL SELECT 'REST APIs' AS skill_name, 'REST API' AS alias
  UNION ALL SELECT 'REST APIs' AS skill_name, 'RESTful API' AS alias
  UNION ALL SELECT 'REST APIs' AS skill_name, 'RESTful APIs' AS alias
  UNION ALL SELECT 'REST APIs' AS skill_name, 'RESTful basics' AS alias
  UNION ALL SELECT 'Unit Testing' AS skill_name, 'unit test' AS alias
  UNION ALL SELECT 'Unit Testing' AS skill_name, 'JUnit' AS alias
  UNION ALL SELECT 'Unit Testing' AS skill_name, 'Jest' AS alias
  UNION ALL SELECT 'Unit Testing' AS skill_name, 'pytest' AS alias
  UNION ALL SELECT 'Unit Testing' AS skill_name, 'Mockito' AS alias
  UNION ALL SELECT 'Design Patterns' AS skill_name, 'design pattern' AS alias
  UNION ALL SELECT 'Design Patterns' AS skill_name, 'singleton' AS alias
  UNION ALL SELECT 'Design Patterns' AS skill_name, 'factory pattern' AS alias
  UNION ALL SELECT 'Debugging' AS skill_name, 'troubleshooting' AS alias
  UNION ALL SELECT 'Debugging' AS skill_name, 'root cause analysis' AS alias
  UNION ALL SELECT 'Agile' AS skill_name, 'Scrum' AS alias
  UNION ALL SELECT 'Agile' AS skill_name, 'Kanban' AS alias
  UNION ALL SELECT 'Java' AS skill_name, 'JDK' AS alias
  UNION ALL SELECT 'Java' AS skill_name, 'Java 17' AS alias
  UNION ALL SELECT 'Java' AS skill_name, 'Java 21' AS alias
  UNION ALL SELECT 'Spring Boot' AS skill_name, 'Spring' AS alias
  UNION ALL SELECT 'Spring Boot' AS skill_name, 'Spring Framework' AS alias
  UNION ALL SELECT 'MySQL' AS skill_name, 'MariaDB' AS alias
  UNION ALL SELECT 'Microservices' AS skill_name, 'microservice' AS alias
  UNION ALL SELECT 'Microservices' AS skill_name, 'micro-services' AS alias
  UNION ALL SELECT 'Redis' AS skill_name, 'Memcached' AS alias
  UNION ALL SELECT 'Message Queues' AS skill_name, 'message queue' AS alias
  UNION ALL SELECT 'Message Queues' AS skill_name, 'RabbitMQ' AS alias
  UNION ALL SELECT 'Message Queues' AS skill_name, 'SQS' AS alias
  UNION ALL SELECT 'Message Queues' AS skill_name, 'ActiveMQ' AS alias
  UNION ALL SELECT 'Authentication' AS skill_name, 'OAuth' AS alias
  UNION ALL SELECT 'Authentication' AS skill_name, 'OAuth2' AS alias
  UNION ALL SELECT 'Authentication' AS skill_name, 'JWT' AS alias
  UNION ALL SELECT 'Authentication' AS skill_name, 'SSO' AS alias
  UNION ALL SELECT 'Authentication' AS skill_name, 'Spring Security' AS alias
  UNION ALL SELECT 'Docker' AS skill_name, 'containerization' AS alias
  UNION ALL SELECT 'Docker' AS skill_name, 'Dockerfile' AS alias
  UNION ALL SELECT 'Docker' AS skill_name, 'docker-compose' AS alias
  UNION ALL SELECT 'JavaScript' AS skill_name, 'JS' AS alias
  UNION ALL SELECT 'JavaScript' AS skill_name, 'ES6' AS alias
  UNION ALL SELECT 'JavaScript' AS skill_name, 'ECMAScript' AS alias
  UNION ALL SELECT 'TypeScript' AS skill_name, 'TS' AS alias
  UNION ALL SELECT 'React' AS skill_name, 'React.js' AS alias
  UNION ALL SELECT 'React' AS skill_name, 'ReactJS' AS alias
  UNION ALL SELECT 'React' AS skill_name, 'Next.js' AS alias
  UNION ALL SELECT 'HTML' AS skill_name, 'HTML5' AS alias
  UNION ALL SELECT 'CSS' AS skill_name, 'CSS3' AS alias
  UNION ALL SELECT 'CSS' AS skill_name, 'SCSS' AS alias
  UNION ALL SELECT 'CSS' AS skill_name, 'Sass' AS alias
  UNION ALL SELECT 'CSS' AS skill_name, 'Tailwind' AS alias
  UNION ALL SELECT 'CSS' AS skill_name, 'Tailwind CSS' AS alias
  UNION ALL SELECT 'Responsive Design' AS skill_name, 'responsive layout' AS alias
  UNION ALL SELECT 'Responsive Design' AS skill_name, 'mobile-first' AS alias
  UNION ALL SELECT 'Responsive Design' AS skill_name, 'media queries' AS alias
  UNION ALL SELECT 'Web Accessibility' AS skill_name, 'accessibility' AS alias
  UNION ALL SELECT 'Web Accessibility' AS skill_name, 'WCAG' AS alias
  UNION ALL SELECT 'Web Accessibility' AS skill_name, 'ARIA' AS alias
  UNION ALL SELECT 'Web Accessibility' AS skill_name, 'a11y' AS alias
  UNION ALL SELECT 'State Management' AS skill_name, 'Redux' AS alias
  UNION ALL SELECT 'State Management' AS skill_name, 'Zustand' AS alias
  UNION ALL SELECT 'State Management' AS skill_name, 'MobX' AS alias
  UNION ALL SELECT 'State Management' AS skill_name, 'Context API' AS alias
  UNION ALL SELECT 'Web Performance' AS skill_name, 'Core Web Vitals' AS alias
  UNION ALL SELECT 'Web Performance' AS skill_name, 'Lighthouse' AS alias
  UNION ALL SELECT 'Web Performance' AS skill_name, 'lazy loading' AS alias
  UNION ALL SELECT 'Web Performance' AS skill_name, 'page load time' AS alias
  UNION ALL SELECT 'Node.js' AS skill_name, 'NodeJS' AS alias
  UNION ALL SELECT 'Node.js' AS skill_name, 'Express.js' AS alias
  UNION ALL SELECT 'MongoDB' AS skill_name, 'Mongo' AS alias
  UNION ALL SELECT 'MongoDB' AS skill_name, 'Mongoose' AS alias
  UNION ALL SELECT 'Excel' AS skill_name, 'Microsoft Excel' AS alias
  UNION ALL SELECT 'Excel' AS skill_name, 'spreadsheets' AS alias
  UNION ALL SELECT 'Excel' AS skill_name, 'pivot table' AS alias
  UNION ALL SELECT 'Excel' AS skill_name, 'VLOOKUP' AS alias
  UNION ALL SELECT 'Data Visualization' AS skill_name, 'data visualisation' AS alias
  UNION ALL SELECT 'Data Visualization' AS skill_name, 'Matplotlib' AS alias
  UNION ALL SELECT 'Data Visualization' AS skill_name, 'Seaborn' AS alias
  UNION ALL SELECT 'Data Visualization' AS skill_name, 'Plotly' AS alias
  UNION ALL SELECT 'Statistics' AS skill_name, 'statistical analysis' AS alias
  UNION ALL SELECT 'Statistics' AS skill_name, 'hypothesis testing' AS alias
  UNION ALL SELECT 'Python' AS skill_name, 'Python3' AS alias
  UNION ALL SELECT 'Python' AS skill_name, 'Python 3' AS alias
  UNION ALL SELECT 'Data Cleaning' AS skill_name, 'data cleansing' AS alias
  UNION ALL SELECT 'Data Cleaning' AS skill_name, 'data wrangling' AS alias
  UNION ALL SELECT 'Data Cleaning' AS skill_name, 'data preprocessing' AS alias
  UNION ALL SELECT 'Dashboards' AS skill_name, 'dashboard' AS alias
  UNION ALL SELECT 'Power BI' AS skill_name, 'PowerBI' AS alias
  UNION ALL SELECT 'Critical Thinking' AS skill_name, 'analytical thinking' AS alias
  UNION ALL SELECT 'Critical Thinking' AS skill_name, 'problem solving' AS alias
  UNION ALL SELECT 'Critical Thinking' AS skill_name, 'problem-solving' AS alias
  UNION ALL SELECT 'Communication' AS skill_name, 'stakeholder management' AS alias
  UNION ALL SELECT 'Communication' AS skill_name, 'cross-functional' AS alias
  UNION ALL SELECT 'Machine Learning' AS skill_name, 'ML' AS alias
  UNION ALL SELECT 'Machine Learning' AS skill_name, 'machine-learning' AS alias
  UNION ALL SELECT 'Machine Learning' AS skill_name, 'supervised learning' AS alias
  UNION ALL SELECT 'Feature Engineering' AS skill_name, 'feature selection' AS alias
  UNION ALL SELECT 'Model Evaluation' AS skill_name, 'cross-validation' AS alias
  UNION ALL SELECT 'Model Evaluation' AS skill_name, 'precision and recall' AS alias
  UNION ALL SELECT 'Model Evaluation' AS skill_name, 'ROC AUC' AS alias
  UNION ALL SELECT 'Model Evaluation' AS skill_name, 'F1 score' AS alias
  UNION ALL SELECT 'Pandas' AS skill_name, 'DataFrame' AS alias
  UNION ALL SELECT 'Scikit-learn' AS skill_name, 'sklearn' AS alias
  UNION ALL SELECT 'Scikit-learn' AS skill_name, 'scikit learn' AS alias
  UNION ALL SELECT 'ETL' AS skill_name, 'ELT' AS alias
  UNION ALL SELECT 'ETL' AS skill_name, 'extract transform load' AS alias
  UNION ALL SELECT 'Data Pipelines' AS skill_name, 'data pipeline' AS alias
  UNION ALL SELECT 'Apache Spark' AS skill_name, 'Spark' AS alias
  UNION ALL SELECT 'Apache Spark' AS skill_name, 'PySpark' AS alias
  UNION ALL SELECT 'Kafka' AS skill_name, 'Apache Kafka' AS alias
  UNION ALL SELECT 'Kafka' AS skill_name, 'Kafka Streams' AS alias
  UNION ALL SELECT 'Data Warehousing' AS skill_name, 'data warehouse' AS alias
  UNION ALL SELECT 'Data Warehousing' AS skill_name, 'Snowflake' AS alias
  UNION ALL SELECT 'Data Warehousing' AS skill_name, 'Redshift' AS alias
  UNION ALL SELECT 'Data Warehousing' AS skill_name, 'BigQuery' AS alias
  UNION ALL SELECT 'Airflow' AS skill_name, 'Apache Airflow' AS alias
  UNION ALL SELECT 'Cloud Storage' AS skill_name, 'S3' AS alias
  UNION ALL SELECT 'Cloud Storage' AS skill_name, 'Amazon S3' AS alias
  UNION ALL SELECT 'Cloud Storage' AS skill_name, 'Blob Storage' AS alias
  UNION ALL SELECT 'Cloud Storage' AS skill_name, 'GCS' AS alias
  UNION ALL SELECT 'Cloud Storage' AS skill_name, 'Google Cloud Storage' AS alias
  UNION ALL SELECT 'Data Modeling' AS skill_name, 'data modelling' AS alias
  UNION ALL SELECT 'Data Modeling' AS skill_name, 'schema design' AS alias
  UNION ALL SELECT 'Data Modeling' AS skill_name, 'star schema' AS alias
  UNION ALL SELECT 'Deep Learning' AS skill_name, 'neural network' AS alias
  UNION ALL SELECT 'Deep Learning' AS skill_name, 'neural networks' AS alias
  UNION ALL SELECT 'Deep Learning' AS skill_name, 'CNN' AS alias
  UNION ALL SELECT 'Deep Learning' AS skill_name, 'LSTM' AS alias
  UNION ALL SELECT 'PyTorch' AS skill_name, 'torch' AS alias
  UNION ALL SELECT 'Model Deployment' AS skill_name, 'model serving' AS alias
  UNION ALL SELECT 'Model Deployment' AS skill_name, 'deployed models' AS alias
  UNION ALL SELECT 'Model Deployment' AS skill_name, 'deployed the model' AS alias
  UNION ALL SELECT 'MLOps' AS skill_name, 'ML Ops' AS alias
  UNION ALL SELECT 'MLOps' AS skill_name, 'MLflow' AS alias
  UNION ALL SELECT 'MLOps' AS skill_name, 'Kubeflow' AS alias
  UNION ALL SELECT 'LLM APIs' AS skill_name, 'LLM' AS alias
  UNION ALL SELECT 'LLM APIs' AS skill_name, 'OpenAI API' AS alias
  UNION ALL SELECT 'LLM APIs' AS skill_name, 'Claude API' AS alias
  UNION ALL SELECT 'LLM APIs' AS skill_name, 'Gemini API' AS alias
  UNION ALL SELECT 'LLM APIs' AS skill_name, 'large language model' AS alias
  UNION ALL SELECT 'Prompt Engineering' AS skill_name, 'prompt design' AS alias
  UNION ALL SELECT 'Prompt Engineering' AS skill_name, 'prompting' AS alias
  UNION ALL SELECT 'Retrieval-Augmented Generation' AS skill_name, 'RAG' AS alias
  UNION ALL SELECT 'Retrieval-Augmented Generation' AS skill_name, 'retrieval augmented generation' AS alias
  UNION ALL SELECT 'Vector Databases' AS skill_name, 'vector database' AS alias
  UNION ALL SELECT 'Vector Databases' AS skill_name, 'vector store' AS alias
  UNION ALL SELECT 'Vector Databases' AS skill_name, 'Pinecone' AS alias
  UNION ALL SELECT 'Vector Databases' AS skill_name, 'FAISS' AS alias
  UNION ALL SELECT 'Vector Databases' AS skill_name, 'Chroma' AS alias
  UNION ALL SELECT 'Vector Databases' AS skill_name, 'pgvector' AS alias
  UNION ALL SELECT 'Cost Management' AS skill_name, 'cost optimization' AS alias
  UNION ALL SELECT 'Cost Management' AS skill_name, 'cost optimisation' AS alias
  UNION ALL SELECT 'Cost Management' AS skill_name, 'FinOps' AS alias
  UNION ALL SELECT 'Cost Management' AS skill_name, 'cloud costs' AS alias
  UNION ALL SELECT 'CI/CD' AS skill_name, 'CI CD' AS alias
  UNION ALL SELECT 'CI/CD' AS skill_name, 'continuous integration' AS alias
  UNION ALL SELECT 'CI/CD' AS skill_name, 'continuous delivery' AS alias
  UNION ALL SELECT 'CI/CD' AS skill_name, 'GitHub Actions' AS alias
  UNION ALL SELECT 'CI/CD' AS skill_name, 'GitLab CI' AS alias
  UNION ALL SELECT 'Kubernetes' AS skill_name, 'K8s' AS alias
  UNION ALL SELECT 'Kubernetes' AS skill_name, 'Helm' AS alias
  UNION ALL SELECT 'Kubernetes' AS skill_name, 'EKS' AS alias
  UNION ALL SELECT 'Kubernetes' AS skill_name, 'GKE' AS alias
  UNION ALL SELECT 'Linux' AS skill_name, 'Ubuntu' AS alias
  UNION ALL SELECT 'Linux' AS skill_name, 'Unix' AS alias
  UNION ALL SELECT 'Infrastructure as Code' AS skill_name, 'IaC' AS alias
  UNION ALL SELECT 'Infrastructure as Code' AS skill_name, 'CloudFormation' AS alias
  UNION ALL SELECT 'Infrastructure as Code' AS skill_name, 'Ansible' AS alias
  UNION ALL SELECT 'AWS' AS skill_name, 'Amazon Web Services' AS alias
  UNION ALL SELECT 'AWS' AS skill_name, 'EC2' AS alias
  UNION ALL SELECT 'Monitoring' AS skill_name, 'Prometheus' AS alias
  UNION ALL SELECT 'Monitoring' AS skill_name, 'Grafana' AS alias
  UNION ALL SELECT 'Monitoring' AS skill_name, 'Datadog' AS alias
  UNION ALL SELECT 'Monitoring' AS skill_name, 'observability' AS alias
  UNION ALL SELECT 'Shell Scripting' AS skill_name, 'Bash' AS alias
  UNION ALL SELECT 'Shell Scripting' AS skill_name, 'shell script' AS alias
  UNION ALL SELECT 'Shell Scripting' AS skill_name, 'shell scripts' AS alias
  UNION ALL SELECT 'Jenkins' AS skill_name, 'Jenkinsfile' AS alias
  UNION ALL SELECT 'Networking Basics' AS skill_name, 'TCP/IP' AS alias
  UNION ALL SELECT 'Networking Basics' AS skill_name, 'DNS' AS alias
  UNION ALL SELECT 'Networking Basics' AS skill_name, 'VPC' AS alias
  UNION ALL SELECT 'Networking Basics' AS skill_name, 'load balancer' AS alias
  UNION ALL SELECT 'Networking Basics' AS skill_name, 'subnets' AS alias
  UNION ALL SELECT 'Azure' AS skill_name, 'Microsoft Azure' AS alias
  UNION ALL SELECT 'Cloud Security' AS skill_name, 'IAM' AS alias
  UNION ALL SELECT 'Cloud Security' AS skill_name, 'encryption at rest' AS alias
  UNION ALL SELECT 'Cloud Security' AS skill_name, 'least privilege' AS alias
) a ON a.skill_name = s.name;
