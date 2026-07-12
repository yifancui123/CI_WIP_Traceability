# Requirements and design

## User Story + Acceptance Criteria (Definition of Done):

### As an operator (the person who moves components): 

1. I want to add components to the product, so that I can make sure their location is moved.
   	- DoD: move the component from the current location to the product location.
2. I want to edit the quantity of components as I add them to the product, so that I can correct their usage.
    - DoD: use the correct quantities when adding components to the system.
3. I want to check the quantity and status for the job number, so that I can verify the status of this component and reconcile the quantity on hand with the system.
   - DoD: scan the component job number, go to the detailed info page to check the system quantity and status.
4. I want to add the new component to a job number, so that it can be recorded in the system.
   - DoD: add quantity to an empty job number.
5. I want to scrap the bad-quality components, so that the on-hand quantity and the system quantity can match.
   - DoD: remove the quantity associated with this job number from the system and label the failure code as the reason. 

### As a leader (the person who manages stock):

1. I want to have all the functions the team member has, so that I can use them.
   - DoD: The leader has all the team members' functions.
2. I want to retrieve the job number from storage and send it to the production line, so that components can be added to production.
   - DoD: move the components under this job number (fixed quantity) from the storage location to the product location
3. I want to create a new job number, so that the team member can record the quantity of this job.
   - DoD: create a unique job number under a specific part number. 

3. I want to check the components' status, so that I can see whether the job is ready to use, on hold, or scrapped.
   - DoD: scan a job number can see the current status.
4. I want to check the transaction history for the components (job numbers), so that I can better manage traceability.
   - DoD: scan the job number/part number to see the history of truncations, including time, user, status, quantity, location... (all the information)
5. I want to delete the job number I accidentally created, so that I can keep the open job number as low as possible. 
   - DoD: deleted the open job number (it must be empty), and the system can never reuse it. 
6. I want to order the components when the quantity is low, so that i can keep the production line running.
   - DoD: create an order list; according to this list, the leader creates a new job number for each part number of each component. And edit the quantity of the new job number. The location will be the default (storage) in the beginning.
7. I want to have a daily report to summarise today's work, so that I can have an overview of the inventory. 









## Requirements

### Functional

### Non-Functional





## Design and Architecture







## Testing User Acceptance Tests

– Define test cases for user stories and requirements that your team decided to test as part of user

acceptance testing. Ensure that these tests cover the most important aspects of the two programs.

– List the user stories and requirements that were the source for the user acceptance tests.

– Include a table that gives an overview of the acceptance tests and tells whether they passed or

failed.

### System Tests

– Include a table that lists the system tests, that weren’t acceptance tests. These are tests that run

the entire program, and are executed by the team to test features they added themselves.

– Include in that table the outcome for each system test, including an overview of the test and

whether it passed or failed.

### Unit Tests

– Select at least four sub-components of your animation.

– Include a table of lists for each component of their unit tests. Include the test inputs, the expected

output/outcome, and whether the test failed or passed.