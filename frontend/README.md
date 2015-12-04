# Reimbursement Frontend Webapplication

## Run the app locally

1. [Install Node.js][]
2. Download and extract the starter code from the Bluemix UI
3. cd into the app directory
4. Run `npm install` to install the app's dependencies
5. Run `npm start` to start the app
6. Access the running app in a browser at http://localhost:6002

## Deploy to Bluemix CloudFoundry

1. Connect to Bluemix '''cf api https://api.ng.bluemix.net'''
2. Login to Bluemix API '''cf login -u maxim.tschumak@campus.tu-berlin.de -o suxxex@freenet.de -s ec\_assignment'''
3. Deploy to Bluemix '''cf push reimbursement-frontend'''
4. Check the production instances: http://reimbursement-frontend.mybluemix.net/
