Cypress.Commands.add('waitForPageLoad', () => {
  cy.get('h4').should('contain', 'Perfil do Utilizador')
  
  cy.get('h6').contains('Seus Veículos').should('be.visible')
})

Cypress.Commands.add('fillField', (selector, value) => {
  cy.get(selector).clear().type(value)
})

Cypress.Commands.add('addCar', (brand, model, plate, battery) => {
  cy.get('input[name="brand"]').clear().type(brand)
  cy.get('input[name="model"]').clear().type(model)
  cy.get('input[name="plate"]').clear().type(plate)
  cy.get('input[name="batteryCapacity"]').clear().type(battery)
  
  cy.get('button').contains('Adicionar Carro').click()
})

Cypress.Commands.add('shouldHaveCarInList', (brand, model, plate) => {
  cy.get('.MuiGrid-container .MuiGrid-item')
    .should('contain', `${brand} ${model}`)
    .and('contain', `Matrícula: ${plate}`)
})

Cypress.Commands.add('shouldShowAlert', (message, type = 'success') => {
  cy.get('.MuiSnackbar-root .MuiAlert-root', { timeout: 8000 })
    .should('be.visible')
    .and('contain', message)
    
  if (type) {
    cy.get('.MuiAlert-root').should('have.class', `MuiAlert-${type}`)
  }
})

Cypress.Commands.add('waitForAlertToDisappear', () => {
  cy.get('.MuiSnackbar-root', { timeout: 5000 }).should('not.exist')
})

Cypress.Commands.add('resetForm', () => {
  cy.get('input[name="brand"]').should('have.value', '')
  cy.get('input[name="model"]').should('have.value', '')
  cy.get('input[name="plate"]').should('have.value', '')
  cy.get('input[name="batteryCapacity"]').should('have.value', '')
})

Cypress.Commands.add('editCar', (brand, model) => {
  cy.get('.MuiGrid-item')
    .contains(`${brand} ${model}`)
    .parent()
    .parent()
    .find('button[color="primary"]')
    .click()
})

Cypress.Commands.add('deleteCar', (brand, model) => {
  cy.get('.MuiGrid-item')
    .contains(`${brand} ${model}`)
    .parent()
    .parent()
    .find('button[color="error"]')
    .click()
})

Cypress.Commands.add('checkBackendHealth', () => {
  cy.request({
    method: 'GET',
    url: 'http://localhost:38945/actuator/health',
    failOnStatusCode: false
  }).then((response) => {
    if (response.status !== 200) {
      cy.log('⚠️ Backend pode não estar rodando')
    }
  })
})