### The Use of AI in Cochlear Implant Component Tracking

#### Background

This traceability system will track the components used in cochlear implant (CI) manufacturing. The goal of this project is to use a database to track components as they move through the production process and visualise real-time data. To improve efficiency, LLMs will also be integrated into this project.  The components are used for CI assembly; the implant assembly process itself is outside the scope of this version.

 ***A) Current state of the art***

Some traceability systems have evolved from basic batch tracking into AI-driven, end-to-end digital ecosystems. However, many places still rely on manual records and spreadsheets, which lead to the challenges below.

***B) Key challenges in the production flow***

1. The on-hand quantity does not match the system quantity, and users physically recount stock to correct the records.
1. Errors occur during movements, such as negative or duplicate movements.
1. Non-conforming components are still being moved through the system.
1. Users manually check the components' information, and there is no daily summary.

 ***C) Scopes***

The components are used for CI assembly. In this project, each component's movement is tracked from its creation through storage and up to its delivery to the assembly stream. Once delivered, the component's status is set to shipped, marking it as no longer part of the system's active stock. The implant assembly process itself is outside the scope of this version.

#### Aims

1. To resolve challenge 1, we will build a movement history record to accurately track quantity changes throughout the production flow, thereby minimising manual cycle counts.
1. To resolve challenge 2, we will set rules to stop and warn the user when a movement would result in a negative quantity and to reject duplicate movements. 
1. To resolve challenge 3, we will build functions that prevent non-conforming components from continuing through the flow and mark them as scrapped, while keeping their full record in the system. 
1. To resolve challenge 4, we will build an AI agent to summarise the day's events (Details in Use of AI).  

#### Methodology

***A) Database operations*** (Below is the relationship of my database entities.)

1) Each part number represents one kind of item. Under each part number, stock is divided into batches, each with a unique job number and a smaller amount. Each job is further divided into components, and each component has its own component ID.
1) Each component records its current location, quantity, and status; a failure code is set on non-conforming components to record the reason. The performing operator is recorded on every movement and quality check, not on the component itself.
1) Movement history records the changes in quantities, locations, time and performing operators. These history records are append-only: they are never edited or deleted, which is the basis of traceability.

***B) Data Presentation***

We will use a web application that users interact with, displaying the database tables in a form resembling an Excel table with detailed information. Tech stack: React frontend, Java Spring Boot backend, PostgreSQL database, REST API, React dashboard or Power BI for visualisation, and an LLM API for the AI features.

***C) Use of AI***

1. **Daily summary:** The system automatically writes a short report covering shortage prediction, reorder recommendations, quality analysis, and anomaly detection. Detection is performed by fixed rules in the database; the AI only writes the narrative based on those verified numbers.
2. **Chat assistant:** A Q&A chat box so users can get answers without digging through the data themselves.
3. **Operator agent:** An assistant that carries out the user's commands, with every action requiring approval before it runs.

In future development, the system can be scaled to cover the assembly process and track finished products through their assembly operations.











---













### The Use of AI in Cochlear Implant WIP (work-in-progress) Tracking

#### Background

The goal of this project is to use a database to track the movement history of Cochlear Implants (CIs) as they move through the production process and visualise real-time data. To improve efficiency, LLMs will also be integrated into this project. This traceability system will record the number of CIs ready at each operation, the number moved to the next operation, and any changes due to scrap or hold.

 ***A) Current state of the art***

Some traceability systems have evolved from basic batch tracking into AI-driven, end-to-end digital ecosystems. However, many places still rely on manual records and spreadsheets, which lead to the challenges below.

***B) Key challenges in the production flow***

1. The on-hand quantity does not match the system quantity due to scrap or on hold, so that users physically recount stock to correct the records.
1. Errors occur during movements, such as negative or duplicate movements.
1. Non-conforming components are still being moved through the system.
1. Users manually check the assembled CIs status, and there is no daily summary or visibility into priority.

#### Aims

1. To resolve challenge 1, we will build a movement history record to accurately track quantity changes throughout the production flow and minus or record the quantities of effect CIs , thereby minimising manual cycle counts.
1. To resolve challenge 2, we will set rules to stop and warn the user when a movement would result in a negative quantity and to reject duplicate movements. 
1. To resolve challenge 3, we will build functions that prevent non-conforming components from continuing through the flow and mark them as scrapped, while keeping their full record in the system. 
1. To resolve challenge 4, we will build an AI agent to summarise the day's events (Details in Use of AI).  

#### Methodology

***A) Database operations*** (Below is the relationship of my database entities.)

1) Each part number represents one kind of CI serial. Under each part number, stock is divided into batches, each with a unique job number and a smaller amount. Each job is further divided into components, and each component has its own component ID.
1) Each component records its current location(operation), quantity, and status; a failure code is set on non-conforming components to record the reason. The performing operator is recorded on every movement and quality check, not on the component itself.
1) Movement history records the changes in quantities, locations, time and performing operators. These history records are append-only: they are never edited or deleted, which is the basis of traceability.

***B) Data Presentation***

We will use a web application that users interact with, displaying the database tables in a form resembling an Excel table with detailed information. Tech stack: React frontend, Java Spring Boot backend, PostgreSQL database, REST API, React dashboard or Power BI for visualisation, and an LLM API for the AI features.

***C) Use of AI***

1. **Daily summary:** The system automatically writes a short report covering unmet production plans, serial prioritisation recommendations, quality analysis, and anomaly detection. Fixed rules in the database perform detection; the AI only writes the narrative based on those verified numbers.
2. **Chat assistant:** A Q&A chat box so users can get answers without digging through the data themselves.
3. **Operator agent:** An assistant that carries out the user's commands, with every action requiring approval before it runs.

