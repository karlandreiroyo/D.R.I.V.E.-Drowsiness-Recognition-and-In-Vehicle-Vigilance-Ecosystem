import { Router, Request, Response } from 'express';
import { body, validationResult } from 'express-validator';

const router: Router = Router();

interface User {
    fullName: string;
    contact: string;
    password: string;
    createdAt: string;
}

const registeredUsers: User[] = [];

const isPHPhoneNumber = (phone: string): boolean => {
    return /^(09|\+639)\d{9}$/.test(phone);
};

const isValidEmail = (email: string): boolean => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

router.get('/users', (_req: Request, res: Response) => {
    return res.status(200).json({
        success: true,
        count: registeredUsers.length,
        users: registeredUsers.map(u => ({
            fullName: u.fullName,
            contact: u.contact,
            createdAt: u.createdAt
        }))
    });
});

router.post(
    '/signup',
    [
        body('fullName')
            .trim()
            .notEmpty().withMessage('Full name is required.')
            .isLength({ min: 1, max: 50 }).withMessage('Full name must be between 1 and 50 characters long.'),

        body('email')
            .custom((value, { req }) => {
                const contact = String(value || req.body?.emailOrPhone || req.body?.identifier || '').trim();

                if (!contact) {
                    throw new Error('Please enter your email address or phone number.');
                }

                const isEmail = isValidEmail(contact);
                const isPhone = isPHPhoneNumber(contact);

                if (!isEmail && !isPhone) {
                    throw new Error('Enter a valid email address or Philippine phone number (09xxxxxxxxx or +639xxxxxxxxx).');
                }
                return true;
            }),

        body('password')
            .notEmpty().withMessage('Password is required.')
            .isStrongPassword({
                minLength: 8,
                minLowercase: 1,
                minUppercase: 1,
                minNumbers: 1,
                minSymbols: 1
            })
            .withMessage('Password must be at least 8 characters long and include 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.'),

        body('confirmPassword')
            .notEmpty().withMessage('Please confirm your password.')
            .custom((value: string, { req }) => {
                if (value !== req.body?.password) {
                    throw new Error('Confirm password does not match password.');
                }
                return true;
            })
    ],
    (req: Request, res: Response): Response => {
        const errors = validationResult(req);

        if (!errors.isEmpty()) {
            const firstErrorMsg = errors.array()[0]?.msg || 'Validation failed.';
            const formattedErrors = errors.array().map((err) => ({
                field: err.type === 'field' ? err.path : 'general',
                message: err.msg
            }));

            return res.status(400).json({
                success: false,
                message: firstErrorMsg,
                errors: formattedErrors
            });
        }

        const { fullName, email, emailOrPhone, identifier, password } = req.body;
        const rawContact = String(email || emailOrPhone || identifier || '').trim();
        const normalizedContact = rawContact.toLowerCase();

        const existingUser = registeredUsers.find(u => u.contact === normalizedContact);
        if (existingUser) {
            return res.status(400).json({
                success: false,
                message: 'An account with this email or phone number already exists.'
            });
        }

        const newUser: User = {
            fullName: fullName.trim(),
            contact: normalizedContact,
            password,
            createdAt: new Date().toISOString()
        };

        registeredUsers.push(newUser);

        console.log('✅ New User Registered:', newUser);
        console.log('📋 Current Registered Users List:', registeredUsers);

        return res.status(200).json({
            success: true,
            message: 'Signup successful!',
            user: {
                fullName: newUser.fullName,
                contact: rawContact
            }
        });
    }
);

router.post(
    '/login',
    [
        body()
            .custom((_, { req }) => {
                const contact = String(req.body?.email || req.body?.emailOrPhone || req.body?.identifier || '').trim();
                if (!contact) {
                    throw new Error('Please enter your email address or phone number.');
                }
                if (!req.body?.password) {
                    throw new Error('Please enter your password.');
                }
                return true;
            })
    ],
    (req: Request, res: Response): Response => {
        const errors = validationResult(req);

        if (!errors.isEmpty()) {
            const firstMsg = errors.array()[0]?.msg || 'Login failed.';
            return res.status(400).json({
                success: false,
                message: firstMsg
            });
        }

        const inputContact = String(req.body.email || req.body.emailOrPhone || req.body.identifier || '').trim().toLowerCase();
        const inputPassword = String(req.body.password || '');

        const user = registeredUsers.find(u => u.contact === inputContact);

        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Account not found. Please sign up first.'
            });
        }

        if (user.password !== inputPassword) {
            return res.status(401).json({
                success: false,
                message: 'Incorrect password. Please try again.'
            });
        }

        console.log('🔑 User Logged In:', user.contact);

        return res.status(200).json({
            success: true,
            message: 'Login successful!',
            user: {
                fullName: user.fullName,
                contact: user.contact
            }
        });
    }
);

export default router;
