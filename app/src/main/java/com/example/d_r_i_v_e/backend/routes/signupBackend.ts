import { Router, Request, Response } from 'express';
import { body, validationResult } from 'express-validator';
import { supabase } from '../supabaseClient.js'; // I-adjust ang path kung kinakailangan

const router: Router = Router();

const isPHPhoneNumber = (phone: string): boolean => {
    return /^(09|\+639)\d{9}$/.test(phone);
};

const isValidEmail = (email: string): boolean => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

// GET: Kunin ang lahat ng users mula sa Supabase
router.get('/users', async (_req: Request, res: Response): Promise<Response> => {
    try {
        const { data, error } = await supabase.from('users').select('*');

        if (error) {
            return res.status(400).json({ success: false, message: error.message });
        }

        return res.status(200).json({
            success: true,
            count: data?.length || 0,
            users: data
        });
    } catch (err: any) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

// POST: Signup Route (Direktang magse-save sa Supabase)
router.post(
    '/signup',
    [
        body('fullName').trim().notEmpty().withMessage('Full name is required.'),
        body('email').trim().notEmpty().isEmail().withMessage('Enter a valid email address.'),
        body('phoneNumber').trim().notEmpty().withMessage('Phone number is required.'),
        body('password').isLength({ min: 8 }).withMessage('Password must be at least 8 characters long.'),
        body('emergencyName').trim().notEmpty().withMessage('Emergency contact name is required.'),
        body('emergencyPhone').trim().notEmpty().withMessage('Emergency phone number is required.'),
        body('vehicleType').trim().notEmpty().withMessage('Vehicle type is required.'),
        body('plateNumber').trim().notEmpty().withMessage('Plate number is required.')
    ],
    async (req: Request, res: Response): Promise<Response> => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                message: errors.array()[0]?.msg || 'Validation failed.',
                errors: errors.array()
            });
        }

        const {
            fullName,
            email,
            phoneNumber,
            password,
            emergencyName,
            emergencyPhone,
            vehicleType,
            plateNumber
        } = req.body;

        const normalizedEmail = String(email).trim().toLowerCase();

        try {
            // 1. I-check kung may nag-eexist na ring email sa Supabase
            const { data: existingUser, error: checkError } = await supabase
                .from('users')
                .select('*')
                .eq('email', normalizedEmail)
                .maybeSingle();

            if (existingUser) {
                return res.status(400).json({
                    success: false,
                    message: 'An account with this email already exists.'
                });
            }

            // 2. I-insert ang bagong user data sa Supabase table na 'users'
            const { data, error: insertError } = await supabase
                .from('users')
                .insert([
                    {
                        full_name: fullName.trim(),
                        email: normalizedEmail,
                        phone_number: phoneNumber.trim(),
                        password: password,
                        emergency_name: emergencyName.trim(),
                        emergency_phone: emergencyPhone.trim(),
                        vehicle_type: vehicleType.trim(),
                        plate_number: plateNumber.trim(),
                        created_at: new Date().toISOString()
                    }
                ])
                .select();

            if (insertError) {
                console.error('Supabase Insert Error:', insertError);
                return res.status(500).json({
                    success: false,
                    message: 'Database error: ' + insertError.message
                });
            }

            console.log('✅ New User Successfully Saved to Supabase:', data);

            return res.status(200).json({
                success: true,
                message: 'Account successfully created!',
                user: data?.[0]
            });

        } catch (err: any) {
            console.error('Server Exception:', err);
            return res.status(500).json({
                success: false,
                message: 'Server error: ' + err.message
            });
        }
    }
);

// POST: Login Route (Magbe-verify mula sa Supabase database)
router.post('/login', async (req: Request, res: Response): Promise<Response> => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ success: false, message: 'Email and password are required.' });
    }

    try {
        const normalizedEmail = String(email).trim().toLowerCase();

        const { data: user, error } = await supabase
            .from('users')
            .select('*')
            .eq('email', normalizedEmail)
            .maybeSingle();

        if (error || !user) {
            return res.status(401).json({ success: false, message: 'Account not found. Please sign up first.' });
        }

        if (user.password !== password) {
            return res.status(401).json({ success: false, message: 'Incorrect password. Please try again.' });
        }

        return res.status(200).json({
            success: true,
            message: 'Login successful!',
            user: {
                fullName: user.full_name,
                email: user.email
            }
        });

    } catch (err: any) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

export default router;