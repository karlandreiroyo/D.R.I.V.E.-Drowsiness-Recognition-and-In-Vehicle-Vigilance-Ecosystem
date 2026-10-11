import express, { Application, Request, Response, NextFunction } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import signupRouter from './routes/signupBackend.js';

dotenv.config();

const app: Application = express();
const PORT = Number(process.env.PORT) || 5000;

// 1. Global Middlewares - MUST be placed BEFORE routes
app.use(cors());
app.use(express.json()); // Essential for parsing JSON body in POST requests
app.use(express.urlencoded({ extended: true }));

// 2. Health Check Route
app.get('/', (_req: Request, res: Response) => {
    res.json({
        success: true,
        message: 'Vigil Backend Service is running'
    });
});

// 3. Mount Authentication Routes at /api/auth
// Endpoints:
// - POST http://localhost:5000/api/auth/signup
// - POST http://localhost:5000/api/auth/login
app.use('/api/auth', signupRouter);

// 4. Fallback 404 Handler for Unmatched Routes
app.use((_req: Request, res: Response) => {
    res.status(404).json({
        success: false,
        message: 'Endpoint not found. Valid endpoints are POST /api/auth/signup and POST /api/auth/login'
    });
});

// 5. Global Error Handler
app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
    console.error('Unhandled Server Error:', err);
    res.status(500).json({
        success: false,
        message: 'Internal server error occurred.'
    });
});

// Bind to 0.0.0.0 so both Emulator (10.0.2.2) and Physical Devices (Wi-Fi/ADB) can connect
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Vigil Backend Server listening on http://0.0.0.0:${PORT}`);
});
