# Reimbursement Frontend Web Application

## Run the App locally

1. Install Node.js
2. Run `npm install` to install the app's dependencies
3. Run `npm start` to start the app
4. Access the running app in a browser at http://localhost:6002

## Deploy the App to Bluemix Cloud Foundry

1. Connect to Bluemix `cf api https://api.ng.bluemix.net`
2. Login to Bluemix API `cf login -u maxim.tschumak@campus.tu-berlin.de -o suxxex@freenet.de -s ec\_assignment`
3. Deploy to Bluemix `cf push reimbursement-frontend`
4. Check the production instances: http://reimbursement-frontend.mybluemix.net/
