import express, { Application } from 'express';
import cors from 'cors';
import signupRoutes from './routes/signupBackend.js';

const app: Application = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

app.use('/api/auth', signupRoutes);

app.listen(PORT, () => {
    console.log(`TypeScript Server running on http://localhost:${PORT}`);
});