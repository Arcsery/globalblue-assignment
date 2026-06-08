export function formatDateToLocalIsoDate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

export function extractApiErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null && 'error' in error) {
    const httpError = error as {
      error?: {
        messages?: string[];
        message?: string;
      };
    };

    if (httpError.error?.messages?.length) {
      return httpError.error.messages.join(' ');
    }

    if (httpError.error?.message) {
      return httpError.error.message;
    }
  }

  return 'Something went wrong. Please try again.';
}
