import { Skeleton } from '@mui/material';
import { PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js';
import React, { useState } from 'react';

const PaymentForm = ({ clientSecret, totalPrice }) => {
    const stripe = useStripe();
    const elements = useElements();
    const [errorMessage, setErrorMessage] = useState("");
    const [isProcessing, setIsProcessing] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!stripe || !elements) {
            return;
        }

        setIsProcessing(true);
        setErrorMessage("");

        const { error: submitError } = await elements.submit();
        if (submitError) {
            setErrorMessage(submitError.message);
            setIsProcessing(false);
            return;
        }

        const { error } = await stripe.confirmPayment({
            elements,
            clientSecret,
            confirmParams: {
                // ✅ Uses the active host URL automatically (Netlify or Localhost)
                return_url: `${window.location.origin}/order-confirm`,
            },
        });

        if (error) {
            setErrorMessage(error.message);
            setIsProcessing(false);
        }
    };

    const paymentElementOptions = {
        layout: "tabs",
    };

    const isLoading = !clientSecret || !stripe || !elements;

    return (
        <form onSubmit={handleSubmit} className='max-w-lg mx-auto p-4'>
            <h2 className='text-xl font-semibold mb-4'>Payment Information</h2>
            {isLoading ? (
                <Skeleton height={150} />
            ) : (
                <>
                    {clientSecret && <PaymentElement options={paymentElementOptions} />}
                    {errorMessage && (
                        <div className='text-red-500 mt-2 font-medium'>{errorMessage}</div>
                    )}

                    <button
                        type="submit"
                        className='text-white w-full px-5 py-[10px] bg-black mt-4 rounded-md font-bold disabled:opacity-50'
                        disabled={!stripe || isLoading || isProcessing}>
                        {isProcessing ? "Processing..." : `Pay ₹${Number(totalPrice).toFixed(2)}`}
                    </button>
                </>
            )}
        </form>
    );
};

export default PaymentForm;