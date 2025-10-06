

/**
 * Create a standardized API response
 * @param {number} statusCode - HTTP status code
 * @param {object|string} body - Response body
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const createResponse = (statusCode, body, headers = {}) => {
  return {
    statusCode,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Credentials': true,
      ...headers
    },
    body: typeof body === 'string' ? body : JSON.stringify(body)
  };
};

/**
 * Create a success response (200 OK)
 * @param {object|string} body - Response body
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const success = (body, headers = {}) => {
  return createResponse(200, body, headers);
};

/**
 * Create a created response (201 Created)
 * @param {object|string} body - Response body
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const created = (body, headers = {}) => {
  return createResponse(201, body, headers);
};

/**
 * Create a no content response (204 No Content)
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const noContent = (headers = {}) => {
  return createResponse(204, '', headers);
};

/**
 * Create a bad request response (400 Bad Request)
 * @param {string} message - Error message
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const badRequest = (message = 'Bad Request', headers = {}) => {
  return createResponse(400, { error: message }, headers);
};

/**
 * Create an unauthorized response (401 Unauthorized)
 * @param {string} message - Error message
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const unauthorized = (message = 'Unauthorized', headers = {}) => {
  return createResponse(401, { error: message }, headers);
};

/**
 * Create a forbidden response (403 Forbidden)
 * @param {string} message - Error message
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const forbidden = (message = 'Forbidden', headers = {}) => {
  return createResponse(403, { error: message }, headers);
};

/**
 * Create a not found response (404 Not Found)
 * @param {string} message - Error message
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const notFound = (message = 'Not Found', headers = {}) => {
  return createResponse(404, { error: message }, headers);
};

/**
 * Create a server error response (500 Internal Server Error)
 * @param {string} message - Error message
 * @param {object} headers - Additional headers
 * @returns {object} - API Gateway response object
 */
const serverError = (message = 'Internal Server Error', headers = {}) => {
  return createResponse(500, { error: message }, headers);
};

module.exports = {
  createResponse,
  success,
  created,
  noContent,
  badRequest,
  unauthorized,
  forbidden,
  notFound,
  serverError
};

